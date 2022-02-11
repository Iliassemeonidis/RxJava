package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       // val observable = Observable.just(1, 2, 3, 4)

//        val disposable = dataSours().subscribe({
//            Log.e(TAG, "Элемент под названием $it")
//        }, {
//
//        })

//        val list = listCreate()
//            .subscribeOn(Schedulers.newThread())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe( {
//            text_view.text = it
//        },{
//        })

//        val creation = Operators()
//        creation.exec()

        val source = Sources()
        source.exec()
    }

    fun dataSours(): Observable<Int> {
        return Observable.create { it ->
            for (i in 0..10) {
                Thread.sleep(1000)
                it.onNext(i)
            }
        }
    }


    fun listCreate() : Observable<String> {
        return Observable.create { ent ->
            val list: List<String> = listOf("mdsfsdfs", "afsdasdasd", "asdadsada")
            list.forEach {
                Thread.sleep(5000)
                ent.onNext(it)
            }
        }
    }
}