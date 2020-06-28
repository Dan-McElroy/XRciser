package com.gymondo.xrciser.client

import com.gymondo.xrciser.data.Exercise
import com.gymondo.xrciser.data.PagedResult
import com.gymondo.xrciser.services.ExerciseService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

object ExerciseClient {

    private val pageSize : Int = 20
    private var shownResults : ArrayList<Exercise> = ArrayList()
    private var nextPageUrl : String? = null
    private var filteredCategoryId : Int? = null
    private var filteredSearchTerm : String? = null
    private var resultsChangedPrompt : Subject<Unit> = BehaviorSubject.create()

    var loadingInProgress: BehaviorSubject<Boolean> = BehaviorSubject.create()

    var results : List<Exercise> = shownResults
        get() = shownResults

    var resultsChanged : Observable<Unit> = resultsChangedPrompt
        get() = resultsChangedPrompt

    var selectedCategory : Int? = filteredCategoryId
        get() = filteredCategoryId

    var loadedAllResults: Boolean = false
        get() = !loadingInProgress.value && nextPageUrl == null

    var currentSearchTerm = filteredSearchTerm
        get() = filteredSearchTerm

    init {
        loadingInProgress.onNext(false)

    }

    fun loadExercises(categoryId : Int? = filteredCategoryId, searchTerm: String? = filteredSearchTerm) {

        loadingInProgress.onNext(true)

        val trimmedSearchTerm = searchTerm?.trim()

        val shouldRefresh = shownResults.none()
                || filteredCategoryId != categoryId
                || filteredSearchTerm != trimmedSearchTerm

        filteredCategoryId = categoryId
        filteredSearchTerm = trimmedSearchTerm

        if (shouldRefresh) {
            clearShownResults()
        } else if (nextPageUrl !is String) {
            // Reached the end of all exercises matching current criteria.
            return
        }

        val service = ExerciseService.create()

        val pageRequest = if (shouldRefresh) service.getExercises(filteredCategoryId) else service.getExercisePage(nextPageUrl!!)

        pageRequest
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result -> receivePagedResult(result, ArrayList()) },
                { t -> t.localizedMessage })
    }

    private fun receivePagedResult(result : PagedResult<Exercise>, resultsForPage : List<Exercise> ) {
        if (filteredSearchTerm.isNullOrBlank()) {
            updateShownResults(result.results)
            nextPageUrl = result.next
            loadingInProgress.onNext(false)
            return
        }

        val filteredResults = result.results.filter { exercise -> exercise.nameMatches(filteredSearchTerm!!) }
            .union(resultsForPage)
            .minus(shownResults)
        if (filteredResults.count() >= pageSize) {
            // We don't change nextPageUrl here, as we want to reload this "server page" and take the rest of it
            // for the next "client page".
            updateShownResults(filteredResults.take(pageSize))
            return
        }

        nextPageUrl = result.next
        if (nextPageUrl is String) {
            ExerciseService.create().getExercisePage(nextPageUrl!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> receivePagedResult(result, filteredResults.toList()) },
                    { TODO("Throw an error here to be handled in the activity with a Snackbar") })
        }
        else {
            updateShownResults(filteredResults.toList())
            loadingInProgress.onNext(false)
        }
    }

    private fun updateShownResults(results : List<Exercise>) {
        shownResults.addAll(results)
        resultsChangedPrompt.onNext(Unit)
    }

    private fun clearShownResults() {
        shownResults.clear()
        resultsChangedPrompt.onNext(Unit)
    }

}