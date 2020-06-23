package com.gymondo.xrciser.client

import com.gymondo.xrciser.data.Category
import com.gymondo.xrciser.services.ExerciseService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.collections.HashMap

object CategoryClient {

    private val categories : MutableMap<Int, Category> = HashMap()

    fun getCategory(id: Int) : Single<Category> {
        if (categories.containsKey(id)) {
            return Single.just(categories[id]!!)
        }
        val observable = ExerciseService.create().getCategory(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        observable
            .subscribe { result -> categories[result.id] = result }
        return observable
    }
}