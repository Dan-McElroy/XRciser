package com.gymondo.xrciser.activities

import android.os.Bundle
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.gymondo.xrciser.R
import com.gymondo.xrciser.client.ImageClient
import com.gymondo.xrciser.data.ExerciseImage
import com.gymondo.xrciser.data.ExerciseInfo
import com.gymondo.xrciser.services.ExerciseService
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

const val EXERCISE_ID = "com.gymondo.xrciser.EXERCISE_ID"

class ExerciseInfoActivity : AppCompatActivity() {

    private var exerciseId: Int = -1
    private lateinit var exerciseInfo : ExerciseInfo

    private lateinit var exerciseName: TextView
    private lateinit var categoryName: TextView
    private lateinit var imageList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_info)
        setSupportActionBar(findViewById(R.id.toolbar))

        exerciseName = findViewById(R.id.exercise_name)
        categoryName = findViewById(R.id.category_name)
        imageList = findViewById(R.id.image_list)

        loadExercise()

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun loadExercise() {
        exerciseId = intent.getIntExtra(EXERCISE_ID, -1)

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

        loadImages()
    }

    private fun loadImages() {
        val imageRequest = ImageClient.getImagesForExercise(exerciseId)
        imageRequest.subscribe { images ->
            if (images.isEmpty()) {
                addImageView()
                return@subscribe
            }
            for (image in images) {
                addImageView(image)
            }
        }
    }

    private fun addImageView(image : ExerciseImage? = null ) {
        val imageView = ImageView(this)
        imageList.addView(imageView)
        if (image is ExerciseImage) {
            Picasso.get().load(image.url)
                .placeholder(R.drawable.exercise)
                .error(R.drawable.exercise)
                .into(imageView)
            imageView.contentDescription = exerciseInfo.name
        } else {
            Picasso.get().load(R.drawable.exercise).into(imageView)
        }
    }
}