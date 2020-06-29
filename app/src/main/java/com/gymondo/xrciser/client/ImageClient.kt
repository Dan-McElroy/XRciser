package com.gymondo.xrciser.client

import com.gymondo.xrciser.data.ExerciseImage
import com.gymondo.xrciser.data.PagedResult
import com.gymondo.xrciser.services.ImageService
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object ImageClient {

    private val images : MutableMap<Int, PagedResult<ExerciseImage>> = HashMap()

    fun getMainImageForExercise(exerciseId: Int) : Maybe<ExerciseImage> {
        if (images.containsKey(exerciseId)) {
            return Maybe.just(images[exerciseId]!!.results.sortedBy { image -> image.isMain }.first())
        }
        return ImageService.create().getMain(exerciseId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result -> result.results.first() }
    }

    fun getImagesForExercise(exerciseId: Int) : Maybe<List<ExerciseImage>> {
        if (images.containsKey(exerciseId)) {
            return Maybe.just(images[exerciseId]!!.results)
        }
        val observable = ImageService.create().getPage(exerciseId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        observable
            .subscribe { result -> images[exerciseId] = result }
        return observable.map { result -> result.results }
    }
}