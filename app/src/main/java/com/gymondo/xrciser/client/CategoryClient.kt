package com.gymondo.xrciser.client

import com.gymondo.xrciser.data.Category
import com.gymondo.xrciser.data.PagedResult
import com.gymondo.xrciser.services.CategoryService
import com.gymondo.xrciser.services.ExerciseService
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.collections.HashMap

object CategoryClient {

    private val categories : MutableMap<Int, Category> = HashMap()

    fun getCategory(id: Int) : Maybe<Category> {
        if (categories.containsKey(id)) {
            return Maybe.just(categories[id]!!)
        }
        val observable = CategoryService.create().getCategory(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        observable
            .subscribe { result -> categories[result.id] = result }
        return observable
    }

    fun getAllCached() : List<Category> = categories.values.toList()

    init {
        CategoryService.create().getPage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::initPage)
    }

    private fun initPage(result : PagedResult<Category>) {
        for (category in result.results) {
            categories[category.id] = category
        }
        if (result.next != null) {
            CategoryService.create().getPage(result.next)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::initPage)
        } else {

        }
    }
}