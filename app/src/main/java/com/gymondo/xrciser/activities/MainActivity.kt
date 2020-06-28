package com.gymondo.xrciser.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gymondo.xrciser.R
import com.gymondo.xrciser.client.ExerciseClient
import com.gymondo.xrciser.data.Exercise
import com.gymondo.xrciser.data.PagedResult
import com.gymondo.xrciser.extensions.makeSnackbar
import com.gymondo.xrciser.fragments.CategoryFilterDialogFragment
import com.gymondo.xrciser.services.ExerciseService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CategoryFilterDialogFragment.Filterable {

    lateinit var recyclerView: RecyclerView
    lateinit var loadingSpinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)
        setRecyclerViewScrollListener()

        // Set up adapter
        recyclerView.adapter = ExerciseAdapter(ExerciseClient.results, this)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Set up loading spinner
        loadingSpinner = findViewById(R.id.loading_progress)
        ExerciseClient.loadingInProgress
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { loading -> loadingSpinner.isVisible = loading }

        ExerciseClient.resultsChanged
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { recyclerView.adapter!!.notifyDataSetChanged() }

        topAppBar.setOnMenuItemClickListener { menuItem ->
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
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onFilterChanged(categoryId: Int?) {
        ExerciseClient.loadExercises(categoryId)
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