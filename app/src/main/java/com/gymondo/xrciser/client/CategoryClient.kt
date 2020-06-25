package com.gymondo.xrciser.client

import com.gymondo.xrciser.data.Category
import com.gymondo.xrciser.data.PagedResult
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

    init {
        ExerciseService.create().getCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::initPage)
    }

    private fun initPage(result : PagedResult<Category>) {
        for (category in result.results) {
            categories[category.id] = category
        }
        if (result.next != null) {
            ExerciseService.create().getCategoryPage(result.next)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::initPage)
        } else {

        }
    }
}