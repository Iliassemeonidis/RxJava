package com.example.myapplication

import android.widget.TextView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class Creation(private val textView: TextView) {

    fun exec() {
        Consumer(Producer()).exec(textView)
    }

    class Producer {
        fun just(): Observable<String> {
            return Observable.just("1", "2", "3")
        }

        fun fromIterable(): Observable<String> {
            return Observable.fromIterable(listOf("1", "2", "3"))
        }

        fun interval(): Observable<Long> = Observable.interval(1, TimeUnit.SECONDS)

        // не понимаю в чем отличие
        fun timer(): Observable<Long> = Observable.interval(1, TimeUnit.SECONDS)

        fun range(): Observable<Int> = Observable.range(1,10)

       private fun  randomResultOperation(): Boolean{
           Thread.sleep(Random.nextLong(1000))
           return listOf(true, false, true)[Random.nextInt(2)]
       }

       fun fromCallable(): Observable<Boolean> = Observable.fromCallable {
           return@fromCallable randomResultOperation()
       }

    }

    class Consumer(private val process: Producer) {
        private val stringObserver = object : Observer<String> {
            var disposable: Disposable? = null

            override fun onSubscribe(d: Disposable) {
                disposable = d
                println("Subscribe")
            }

            override fun onNext(t: String) {
                println("onNext: $t")
            }

            override fun onError(e: Throwable) {
                println("onError: ${e.message}")
            }

            override fun onComplete() {
                println("onComplete")
            }

        }

        fun exec(textView: TextView) {
            //execJust()
            // execLambda(textView)
           // execInterval(textView)
          //  execRange(textView)
            execFromCallable(textView)
        }

        private fun execJust() {
            process.just().observeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringObserver)
        }

        private fun execLambda(view: TextView) {
            val observer = process.just()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    it.forEach { item ->
                        Thread.sleep(2000)
                        view.text = item.toString()

                    }
                    //print("Next: $it")
                }, { e ->
                    print(e.message)

                }, {
                    println("onComplete")
                })
        }

        private fun execIterable() {
            process.fromIterable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    stringObserver
                }, {

                })
        }

        private fun execInterval(textView: TextView) {
            process.interval()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    textView.text = it.toString()
                    print("onNext: $it")
                }
        }


        // крайне странное поведение выводит только последний элемент
        private fun execRange(textView: TextView){
            process.range()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    print("onNext: $it")
                    textView.text = it.toString()
                }
        }


        private fun execFromCallable(textView: TextView) {
            process.fromCallable().subscribe{
                textView.text = it.toString()
            }
        }
    }
}