package com.gymondo.xrciser.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.gymondo.xrciser.R
import com.gymondo.xrciser.client.ImageClient
import com.gymondo.xrciser.data.Equipment
import com.gymondo.xrciser.data.ExerciseImage
import com.gymondo.xrciser.data.ExerciseInfo
import com.gymondo.xrciser.data.Muscle
import com.gymondo.xrciser.services.ExerciseService
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

const val EXERCISE_ID = "com.gymondo.xrciser.EXERCISE_ID"

class ExerciseInfoActivity : AppCompatActivity() {

    private var exerciseId: Int = -1
    private lateinit var exerciseInfo : ExerciseInfo

    private lateinit var appBar: MaterialToolbar

    private lateinit var exerciseName: TextView
    private lateinit var categoryName: TextView
    private lateinit var imageList: LinearLayout
    private lateinit var equipmentList: LinearLayout
    private lateinit var muscleList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_info)

        appBar = findViewById(R.id.topAppBar)
        setSupportActionBar(appBar)

        exerciseName = findViewById(R.id.exercise_name)
        categoryName = findViewById(R.id.category_name)
        imageList = findViewById(R.id.image_list)
        equipmentList = findViewById(R.id.equipment_list)
        muscleList = findViewById(R.id.muscle_list)

        loadExercise()
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
        appBar.title = exerciseInfo.name
        exerciseName.text = exerciseInfo.name
        categoryName.text = exerciseInfo.category.name

        loadImages()

        if (exerciseInfo.equipment.any()) {
            exerciseInfo.equipment.forEach(this::addEquipmentText)
        } else {
            addEquipmentText(null)
        }

        // TODO("Split out muscles and secondary muscles in view")
        if (exerciseInfo.allMuscles.any()) {
            exerciseInfo.allMuscles.forEach(this::addMuscleText)
        } else {
            addMuscleText(null)
        }
    }

    private fun loadImages() {
        val imageRequest = ImageClient.getImagesForExercise(exerciseId)
        imageRequest.subscribe( { images ->
            if (images.isEmpty()) {
                addImageView(null)
                return@subscribe
            }
            images.sortedBy { image -> image.isMain }.forEach(this::addImageView)
        },
        { addErrorImage() })

    }

    private fun addImageView(image : ExerciseImage?) {
        val imageView = addImage()
        if (image is ExerciseImage) {
            Picasso.get().load(image.url)
                .placeholder(R.drawable.ic_hourglass_empty)
                .error(R.drawable.ic_error_outline)
                .into(imageView)
            imageView.contentDescription = exerciseInfo.name
        } else {
            Picasso.get().load(R.drawable.exercise)
                .into(imageView)
        }
    }

    private fun addErrorImage() {
        val imageView = addImage()
        Picasso.get().load(R.drawable.ic_error_outline)
            .into(imageView)
    }

    private fun addEquipmentText(equipment: Equipment?) {
        val textView = addTextRow(equipmentList)
        textView.text = if (equipment is Equipment) equipment.name else getString(R.string.none)
    }

    private fun addMuscleText(muscle: Muscle?) {
        val textView = addTextRow(muscleList)
        textView.text = if (muscle is Muscle) muscle.toString() else getString(R.string.none)
    }

    private fun addTextRow(layout : LinearLayout) : TextView {
        val view = LayoutInflater.from(layout.context).inflate(R.layout.exercise_info_text, layout, false) as TextView
        layout.addView(view)
        return view
    }

    private fun addImage() : ImageView {
        val view = LayoutInflater.from(imageList.context).inflate(R.layout.exercise_info_image, null, false) as ImageView
        imageList.addView(view)
        return view
    }
}