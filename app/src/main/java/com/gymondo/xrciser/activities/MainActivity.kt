package com.gymondo.xrciser.activities

import android.app.SearchManager
import android.app.SearchableInfo
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gymondo.xrciser.R
import com.gymondo.xrciser.applications.XRciserApp
import com.gymondo.xrciser.client.ExerciseClient
import com.gymondo.xrciser.fragments.CategoryFilterDialogFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CategoryFilterDialogFragment.Filterable, SearchView.OnQueryTextListener {

    lateinit var recyclerView: RecyclerView
    lateinit var loadingSpinner: ProgressBar
    lateinit var noSearchResultsDisplay: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.top_app_bar))
        recyclerView = findViewById(R.id.recycler_view)
        setRecyclerViewScrollListener()
        XRciserApp.currentActivity = this

        noSearchResultsDisplay = findViewById(R.id.no_search_results)
        noSearchResultsDisplay.visibility = View.INVISIBLE

        // Set up adapter
        recyclerView.adapter = ExerciseAdapter(ExerciseClient.results, this)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Set up loading spinner
        loadingSpinner = findViewById(R.id.loading_progress)
        ExerciseClient.loadingInProgress
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                loading -> loadingSpinner.isVisible = loading
                val noSearchResults = ExerciseClient.results.none()
                        && !(ExerciseClient.currentSearchTerm.isNullOrBlank() || loading)
                noSearchResultsDisplay.visibility = if (noSearchResults) View.VISIBLE else View.INVISIBLE
            }

        ExerciseClient.resultsChanged
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                recyclerView.adapter!!.notifyDataSetChanged()
            }

        top_app_bar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.filter -> {
                    openFilterMenu()
                    true
                }
                else -> false
            }
        }

        ExerciseClient.loadExercises()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)

        val searchItem: MenuItem? = menu?.findItem(R.id.search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: SearchView? = searchItem?.actionView as SearchView
        val searchableInfo: SearchableInfo? = searchManager.getSearchableInfo(componentName)

        searchView?.setSearchableInfo(searchableInfo)
        searchView?.setOnQueryTextListener(this)
        searchView?.setIconifiedByDefault(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        ExerciseClient.loadExercises(searchTerm = query)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        // Prevents new exercises from being loaded
        if (ExerciseClient.currentSearchTerm is String && newText?.isBlank() == true) {
            ExerciseClient.loadExercises(searchTerm = newText)
        }
        return true
    }

    override fun onFilterChanged(categoryId: Int?) {
        ExerciseClient.loadExercises(categoryId = categoryId)
    }

    private fun setRecyclerViewScrollListener() {
        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (ExerciseClient.results.none()) {
                    return
                }
                val linearLayoutManager: LinearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager

                val visibleItemCount = linearLayoutManager.childCount
                val totalItemCount = linearLayoutManager.itemCount
                val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

                val isNotLoadingAndNotLastPage =
                    !(ExerciseClient.loadingInProgress.value || ExerciseClient.loadedAllResults)
                val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
                val isValidFirstItem = firstVisibleItemPosition >= 0
                val totalIsMoreThanVisible = totalItemCount >= ExerciseClient.results.count()

                val shouldLoadMore = isNotLoadingAndNotLastPage && isAtLastItem
                        && isValidFirstItem && totalIsMoreThanVisible

                if (shouldLoadMore) {
                    ExerciseClient.loadExercises()
                }
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
    }

    private fun openFilterMenu() {
        val filterSheet = CategoryFilterDialogFragment.newInstance(ExerciseClient.selectedCategory)
        filterSheet.filterListener = this
        filterSheet.show(supportFragmentManager, "filterDialogFragment")
    }
}