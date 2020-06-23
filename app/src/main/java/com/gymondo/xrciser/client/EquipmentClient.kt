package com.gymondo.xrciser.client

import com.gymondo.xrciser.data.Equipment
import com.gymondo.xrciser.services.ExerciseService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.collections.HashMap

object EquipmentClient {

    private val categories : MutableMap<Int, Equipment> = HashMap()

    fun getEquipment(id: Int) : Observable<Equipment> {
        if (categories.containsKey(id)) {
            return Observable.just(categories[id]!!)
        }
        val observable = ExerciseService.create().getEquipment(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        observable
            .subscribe { result -> categories[result.id] = result }
        return observable
    }
}