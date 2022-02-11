package com.example.myapplication

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Function4
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class Operators {

    fun exec() {
        Consumer(Producer()).exec()
    }

    class Producer {
        fun createJust(): Observable<String> = Observable.just("1", "2", "3", "3")
        fun createJust2(): Observable<String> = Observable.just("4", "5", "6", "7")

    }

    class Consumer(val process: Producer) {

        fun exec() {
            //execTake()
            //execSkip()
            //execMap()
            //execDistinct()
            //execFilter()
            //execMerge()
            //execFlatMap()
            execSwitchMap()
            //execZip()
        }


        private fun execTake() {
            process.createJust()
                .take(2)
                .subscribe({ s ->
                    println("onNext: $s")
                }, {
                    println("onError: ${it.message}")
                })
        }

        private fun execSkip() {
            process.createJust()
                .skip(2)
                .subscribe({
                    println("onNext: $it")
                }, {
                    println("onError: ${it.message}")
                })
        }

        fun execMap() {
            process.createJust()
                .map { it + it }
                .subscribe({
                    println("onNext: $it")
                }, {
                    println("onError: ${it.message}")
                })
        }

        private fun execDistinct() {
            process.createJust()
                .distinct()
                .subscribe({
                    println("onNext: $it")
                }, {
                    println("onError: ${it.message}")
                })
        }

        fun execFilter() {
            process.createJust()
                .filter { it.toInt() > 1 }
                .subscribe({
                    println("onNext: $it")
                }, {
                    println("onError: ${it.message}")
                })
        }

        fun execMerge() {
            process.createJust()
                .mergeWith(process.createJust2())
                .subscribe({
                    println("onNext: $it")
                }, {
                    println("onError: ${it.message}")
                })
        }

        fun execFlatMap() {
            process.createJust()
                .flatMap {
                    val delay = Random.nextInt(1000).toLong()
                    return@flatMap Observable.just(it + "x").delay(delay, TimeUnit.MICROSECONDS)
                }
                .subscribe({
                    println("onNext: $it")
                }, {
                    println("onError: ${it.message}")
                })
        }
        fun execSwitchMap() {
            process.createJust()
                .switchMap {
                    val delay = Random.nextInt(1000).toLong()
                    return@switchMap Observable.just(it + "x").delay(delay, TimeUnit.MICROSECONDS)
                }
                .subscribe({
                    println("onNext: $it")
                }, {
                    println("onError: ${it.message}")
                })
        }

        fun execZip() {
            val observable1 = Observable.just("1").delay(1, TimeUnit.SECONDS)
            val observable2 = Observable.just("2").delay(2, TimeUnit.SECONDS)
            val observable3 = Observable.just("3").delay(3, TimeUnit.SECONDS)
            val observable4 = Observable.just("4").delay(4, TimeUnit.SECONDS)

            Observable.zip(observable1,observable2,observable3,observable4, Function4<String,String,String,String, List<String>>{
                t1,t2,t3,t4 ->
                return@Function4 listOf(t1,t2,t3,t4)
            })
                .subscribeOn(Schedulers.computation())
                .subscribe({
                    println("onNext: $it")
                },{
                    println("onError: ${it.message}")
                })

        }

    }
}