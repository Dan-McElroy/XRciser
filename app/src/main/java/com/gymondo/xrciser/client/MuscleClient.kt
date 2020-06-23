package com.gymondo.xrciser.client

import com.gymondo.xrciser.data.Muscle
import com.gymondo.xrciser.services.ExerciseService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.collections.HashMap

object MuscleClient {

    private val categories : MutableMap<Int, Muscle> = HashMap()

    fun getMuscle(id: Int) : Observable<Muscle> {
        if (categories.containsKey(id)) {
            return Observable.just(categories[id]!!)
        }
        val observable = ExerciseService.create().getMuscle(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        observable
            .subscribe { result -> categories[result.id] = result }
        return observable
    }
}