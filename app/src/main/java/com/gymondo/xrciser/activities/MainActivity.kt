package com.gymondo.xrciser.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gymondo.xrciser.R
import com.gymondo.xrciser.data.Exercise
import com.gymondo.xrciser.data.PagedResult
import com.gymondo.xrciser.fragments.CategoryFilterDialogFragment
import com.gymondo.xrciser.services.ExerciseService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CategoryFilterDialogFragment.CategoryFilterListener {

    var loadingInProgress: Boolean = false
    var allExercises = ArrayList<Exercise>()
    var lastExerciseResult: PagedResult<Exercise>? = null
    lateinit var recyclerView: RecyclerView
    var selectedCategoryId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)
        setRecyclerViewScrollListener()

        // Set up adapter
        recyclerView.adapter = ExerciseAdapter(allExercises, this)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.filter -> {
                    openFilterMenu()
                    true
                }
                else -> false
            }
        }

        loadExercises()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
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
        loadExercises(categoryId)
    }

    private fun loadExercises(categoryId: Int? = null) {

        val shouldRefresh =
            (lastExerciseResult == null && !loadingInProgress)
                || selectedCategoryId != categoryId

        if (shouldRefresh) {
            allExercises.clear()
            recyclerView.adapter!!.notifyDataSetChanged()
        }

        selectedCategoryId = categoryId
        loadingInProgress = true

        val service = ExerciseService.create()
        val exercises = if (shouldRefresh) service.getExercises(categoryId)
                   else service.getExercisePage(lastExerciseResult!!.next)

        exercises
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
            { response : PagedResult<Exercise> ->
                lastExerciseResult = response
                allExercises.addAll(response.results)
                recyclerView.adapter!!.notifyDataSetChanged()
                loadingInProgress = false
            },
            { loadingInProgress = false })
    }

    private fun setRecyclerViewScrollListener() {
        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (lastExerciseResult == null) {
                    return
                }
                val linearLayoutManager: LinearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager

                val visibleItemCount = linearLayoutManager.childCount
                val totalItemCount = linearLayoutManager.itemCount
                val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

                val isNotLoadingAndNotLastPage =
                    !loadingInProgress && lastExerciseResult?.next != null
                val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
                val isValidFirstItem = firstVisibleItemPosition >= 0
                val totalIsMoreThanVisible = totalItemCount >= lastExerciseResult!!.results.count()

                val shouldLoadMore = isNotLoadingAndNotLastPage && isAtLastItem
                        && isValidFirstItem && totalIsMoreThanVisible

                if (shouldLoadMore) {
                    loadExercises()
                }
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
    }

    private fun openFilterMenu() {
        val filterSheet = CategoryFilterDialogFragment.newInstance(selectedCategoryId)
        filterSheet.filterListener = this
        filterSheet.show(supportFragmentManager, "filterDialogFragment")
    }
}