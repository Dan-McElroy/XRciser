package com.gymondo.xrciser.client

import com.gymondo.xrciser.data.ExerciseImage
import com.gymondo.xrciser.data.PagedResult
import com.gymondo.xrciser.services.ExerciseService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object ImageClient {

    private val categories : MutableMap<Int, PagedResult<ExerciseImage>> = HashMap()

    fun getImagesForExercise(exerciseId: Int) : Single<List<ExerciseImage>> {
        if (categories.containsKey(exerciseId)) {
            return Single.just(categories[exerciseId]!!.results)
        }
        val observable = ExerciseService.create().getImages(exerciseId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        observable
            .subscribe { result -> categories[exerciseId] = result }
        return observable.map { result -> result.results }
    }
}