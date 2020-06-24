package com.gymondo.xrciser.activities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gymondo.xrciser.R
import com.gymondo.xrciser.client.CategoryClient
import com.gymondo.xrciser.client.EquipmentClient
import com.gymondo.xrciser.client.ImageClient
import com.gymondo.xrciser.client.MuscleClient
import com.gymondo.xrciser.data.Equipment
import com.gymondo.xrciser.data.Exercise
import com.gymondo.xrciser.data.Muscle
import com.squareup.picasso.Picasso
import com.squareup.picasso.PicassoProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ExerciseAdapter(private var exerciseList: List<Exercise>, private val context: Context)
    : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        return ExerciseViewHolder(LayoutInflater.from(context).inflate(R.layout.exercise_card, parent, false))
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {

        val exercise = exerciseList[position]

        holder.exerciseName.text = exercise.name

        loadCategory(holder, exercise)
        loadImage(holder, exercise)
        loadEquipment(holder, exercise)
        loadMuscles(holder, exercise)
    }

    private fun loadCategory(holder: ExerciseViewHolder, exercise: Exercise) {
        CategoryClient.getCategory(exercise.category)
            .subscribe(
                { response -> holder.categoryName.text = response.name},
                { holder.categoryName.text = "Category not found"})
    }

    private fun loadImage(holder: ExerciseViewHolder, exercise: Exercise) {

        val imageRequest = ImageClient.getImagesForExercise(exercise.id)
        imageRequest.subscribe { images ->
            if (images.isEmpty()) {
                Picasso.get().load(R.drawable.exercise).into(holder.image)
                return@subscribe
            }
            // TODO: Edge(?) case where exercise could have all non-main images
            Picasso.get().load(images[0].url)
                .placeholder(R.drawable.exercise)
                .error(R.drawable.exercise)
                .into(holder.image)
        }
    }

    private fun loadEquipment(holder: ExerciseViewHolder, exercise: Exercise) {

        if (exercise.equipment.none()) {
            holder.equipmentList.text = "None"
        }

        val equipmentRequests : List<Observable<Equipment>> = exercise.equipment.map(EquipmentClient::getEquipment)

        Observable.zip(equipmentRequests) { results: Array<Any> ->
                results.joinToString(", ") { result -> (result as Equipment).name }
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { listText -> holder.equipmentList.text = listText },
                { holder.equipmentList.text = "Equipment not found." }
            )
    }

    private fun loadMuscles(holder: ExerciseViewHolder, exercise: Exercise) {

        val allMuscleIds = exercise.muscles + exercise.secondaryMuscles

        if (allMuscleIds.none()) {
            holder.muscleList.text = "None"
        }

        val muscleRequests : List<Observable<Muscle>> = allMuscleIds.map(MuscleClient::getMuscle)

        Observable.zip(muscleRequests) { results: Array<Any> ->
            results.joinToString(", ") { result -> (result as Muscle).name }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { listText -> holder.muscleList.text = listText },
                { holder.muscleList.text = "Muscle(s) not found." }
            )
    }

    class ExerciseViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView) {

        lateinit var exerciseName: TextView
        lateinit var categoryName: TextView
        lateinit var equipmentList: TextView
        lateinit var muscleList: TextView
        lateinit var image: ImageView

        init {
            exerciseName = itemLayoutView.findViewById(R.id.exercise_name)
            categoryName = itemLayoutView.findViewById(R.id.category_name)
            equipmentList = itemLayoutView.findViewById(R.id.equipment_list)
            muscleList = itemLayoutView.findViewById(R.id.muscle_list)
            image = itemLayoutView.findViewById(R.id.image)
        }
    }

}