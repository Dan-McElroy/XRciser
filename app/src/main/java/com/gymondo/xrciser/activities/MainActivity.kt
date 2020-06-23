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
import com.gymondo.xrciser.data.ExerciseResult
import com.gymondo.xrciser.services.WorkoutManagerService

import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    var exerciseList = ArrayList<Exercise>()
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        recyclerView = findViewById(R.id.recycler_view)

        // Set up adapter
        recyclerView.adapter = ExerciseAdapter(exerciseList, this)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        getData()
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

    private fun getData() {
        val call = WorkoutManagerService.create().getAllExercises()
        call.enqueue(object: Callback<ExerciseResult> {

            override fun onResponse(call: Call<ExerciseResult>?, response: Response<ExerciseResult>?) {
                exerciseList.addAll(response!!.body()!!.results)
                recyclerView.adapter!!.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<ExerciseResult>, t: Throwable) {

            }
        })
    }
}
