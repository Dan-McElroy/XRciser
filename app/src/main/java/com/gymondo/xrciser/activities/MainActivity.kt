package com.gymondo.xrciser.activities

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gymondo.xrciser.R
import com.gymondo.xrciser.data.Exercise
import com.gymondo.xrciser.data.PagedResult
import com.gymondo.xrciser.services.ExerciseService

import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    var loadingInProgress: Boolean = false
    var allExercises = ArrayList<Exercise>()
    var lastExerciseResult: PagedResult<Exercise>? = null
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        recyclerView = findViewById(R.id.recycler_view)
        setRecyclerViewScrollListener()

        // Set up adapter
        recyclerView.adapter = ExerciseAdapter(allExercises, this)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
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

    private fun loadExercises() {

        loadingInProgress = true

        val displayOnLoad = object : Callback<PagedResult<Exercise>> {

            override fun onResponse(
                call: Call<PagedResult<Exercise>>?,
                response: Response<PagedResult<Exercise>>?
            ) {
                lastExerciseResult = response!!.body()!!
                allExercises.addAll(response!!.body()!!.results)
                recyclerView.adapter!!.notifyDataSetChanged()
                loadingInProgress = false
            }

            override fun onFailure(call: Call<PagedResult<Exercise>>, t: Throwable) {
                // TODO: Kill loading UI, report error
                loadingInProgress = false
            }
        }

        val service = ExerciseService.create()

        val call = if (lastExerciseResult == null) service.getExercises()
                   else service.getPage(lastExerciseResult!!.next)

        call.enqueue(displayOnLoad)
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
                    !loadingInProgress && lastExerciseResult!!.next != null
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
}
