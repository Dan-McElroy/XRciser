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

    private const val pageSize : Int = 20
    private var nextPageUrl : String? = null
    private var resultsChangedPrompt : Subject<Unit> = BehaviorSubject.create()

    var loadingInProgress: BehaviorSubject<Boolean> = BehaviorSubject.create()

    var results : ArrayList<Exercise> = ArrayList()
        private set

    var resultsChanged : Observable<Unit> = resultsChangedPrompt
        private set

    var selectedCategory : Int? = null
        private set

    var currentSearchTerm : String? = null
        private set

    var loadedAllResults: Boolean = false
        get() = !loadingInProgress.value && nextPageUrl == null


    init {
        loadingInProgress.onNext(false)

    }

    fun loadExercises(categoryId : Int? = selectedCategory, searchTerm: String? = currentSearchTerm) {

        loadingInProgress.onNext(true)

        val trimmedSearchTerm = searchTerm?.trim()

        val shouldRefresh = results.none()
                || selectedCategory != categoryId
                || currentSearchTerm != trimmedSearchTerm

        selectedCategory = categoryId
        currentSearchTerm = trimmedSearchTerm

        if (shouldRefresh) {
            clearShownResults()
        } else if (nextPageUrl !is String) {
            // Reached the end of all exercises matching current criteria.
            return
        }

        val service = ExerciseService.create()

        val pageRequest = if (shouldRefresh) service.getPage(selectedCategory) else service.getPage(nextPageUrl!!)

        pageRequest
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result -> receivePagedResult(result, ArrayList()) },
                { t -> t.localizedMessage })
    }

    private fun receivePagedResult(result : PagedResult<Exercise>, resultsForPage : List<Exercise> ) {
        if (currentSearchTerm.isNullOrBlank()) {
            updateShownResults(result.results)
            nextPageUrl = result.next
            loadingInProgress.onNext(false)
            return
        }

        val filteredResults = result.results.filter { exercise -> exercise.nameMatches(currentSearchTerm!!) }
            .union(resultsForPage)
            .minus(results)
        if (filteredResults.count() >= pageSize) {
            // We don't change nextPageUrl here, as we want to reload this "server page" and take the rest of it
            // for the next "client page".
            updateShownResults(filteredResults.take(pageSize))
            return
        }

        nextPageUrl = result.next
        if (nextPageUrl is String) {
            ExerciseService.create().getPage(nextPageUrl!!)
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

    private fun updateShownResults(newResults : List<Exercise>) {
        results.addAll(newResults)
        resultsChangedPrompt.onNext(Unit)
    }

    private fun clearShownResults() {
        results.clear()
        resultsChangedPrompt.onNext(Unit)
    }
}