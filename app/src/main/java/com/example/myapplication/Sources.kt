package com.example.myapplication

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observables.ConnectableObservable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class Sources {

    fun exec() {
        Consumer(Producer()).exec()
    }

    class Producer {

        private fun randomResultOperation(): Boolean {
            Thread.sleep(Random.nextLong(1000))
            return listOf(true, false, true)[Random.nextInt(2)]
        }

        fun completable(): Completable = Completable.create { emitter ->
            randomResultOperation().let {
                if (it) {
                    emitter.onComplete()
                } else {
                    emitter.onError(RuntimeException("Error"))
                    return@create
                }
            }
        }

        fun single() = Single.fromCallable {
            return@fromCallable "Some string value"
        }

        fun maybe() = Maybe.create<String> { emitter ->
            randomResultOperation().let {
                if (it) {
                    emitter.onSuccess("Success: $it")
                } else {
                    emitter.onComplete()
                    return@create
                }
            }

        }

        fun hotObservable(): ConnectableObservable<Long> =
            Observable.interval(1, TimeUnit.SECONDS).publish()

        fun publishSubject(): PublishSubject<String> = PublishSubject.create<String>().apply {
            Observable.timer(2, TimeUnit.SECONDS).subscribe {
                onNext("Value from subject")
            }
        }


        fun observable1() = Observable.just("1")
        fun observable2() = Observable.just("2")

    }

    class Consumer(private val producer: Producer) {
        fun exec() {
            //execCompletable()
            //execSingle()
            // execMaybe()
            //execHotObservable()
            //execPublishSubject()
            execComposite()
        }


        private fun execCompletable() {
            producer.completable()
                .subscribe({
                    println("onComplete")
                }, {
                    println("onError: ${it.message}")
                })
        }

        private fun execSingle() {
            producer.single()
                .map { "$it $it" }
                .subscribe({
                    println("onSuccess: $it")
                }, {
                    println("onError: ${it.message}")
                })
        }

        private fun execMaybe() {
            producer.maybe()
                .map { "$it $it" }
                .subscribe({
                    println("onSuccess: $it")
                }, {
                    println("onError: ${it.message}")
                }, {
                    println("onComplete")
                })
        }

        private fun execHotObservable() {
            val hotObservable = producer.hotObservable()

            hotObservable.subscribe {
                println(it)
            }

            hotObservable.connect()

            Thread.sleep(3000)
            hotObservable.subscribe {
                println("delayed: $it")
            }
        }

        private fun execPublishSubject() {
            val subject = producer.publishSubject()

            subject.subscribe({
                println("onNext: $it")
            }, {
                println("onError: ${it.message}")
            })

            subject.onNext("from exec")
        }

        fun execComposite() {
            val compositeDisposable = CompositeDisposable()
            val disposable1 = producer.observable1().subscribe {
                println(it)
            }
            val disposable2 = producer.observable2().subscribe {
                println(it)
            }
            compositeDisposable.addAll(disposable1)
            compositeDisposable.addAll(disposable2)

            //compositeDisposable.clear()
        }
    }
}