package com.gymondo.xrciser.activities

import android.os.Bundle
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.gymondo.xrciser.R
import com.gymondo.xrciser.data.ExerciseInfo
import com.gymondo.xrciser.services.ExerciseService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

const val EXERCISE_ID = "com.gymondo.xrciser.EXERCISE_ID"

class ExerciseInfoActivity : AppCompatActivity() {

    lateinit var exerciseInfo : ExerciseInfo

    lateinit var exerciseName: TextView
    lateinit var categoryName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_info)
        setSupportActionBar(findViewById(R.id.toolbar))

        exerciseName = findViewById(R.id.exercise_name)
        categoryName = findViewById(R.id.category_name)

        loadExercise()

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun loadExercise() {
        val exerciseId = intent.getIntExtra(EXERCISE_ID, -1)

        ExerciseService.create().getExerciseInfo(exerciseId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ info ->
                this.exerciseInfo = info
                renderExercise()
            },
            { TODO("Present error message about not finding the exercise") })
    }

    private fun renderExercise() {
        exerciseName.text = exerciseInfo.name
        categoryName.text = exerciseInfo.category.name
    }
}