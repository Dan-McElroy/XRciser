package com.gymondo.xrciser.activities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
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

        holder.itemView.setOnClickListener(ExerciseCardOnClickListener(exercise))

        holder.exerciseName.text = exercise.name
        holder.image.contentDescription = exercise.name

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
            // TODO("Edge(?) case where exercise could have all non-main images")
            Picasso.get().load(images[0].url)
                .placeholder(R.drawable.ic_hourglass_empty_24px)
                .error(R.drawable.ic_error_outline_24px)
                .into(holder.image)
        }
    }

    private fun loadEquipment(holder: ExerciseViewHolder, exercise: Exercise) {

        val equipment = context.getString(R.string.equipment)

        if (exercise.equipment.none()) {
            holder.equipmentList.text = "$equipment None"
        }

        val equipmentRequests : List<Observable<Equipment>> = exercise.equipment.map(EquipmentClient::getEquipment)

        Observable.zip(equipmentRequests) { results: Array<Any> ->
                results.joinToString(", ") { result -> (result as Equipment).name }
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { listText ->
                    val equipment = context.getString(R.string.equipment)
                    holder.equipmentList.text = "$equipment $listText" },
                { holder.equipmentList.text = "Equipment not found." }
            )
    }

    private fun loadMuscles(holder: ExerciseViewHolder, exercise: Exercise) {

        val muscles = context.getString(R.string.muscles)

        if (exercise.allMuscles.none()) {
            holder.muscleList.text = "$muscles None"
        }

        val muscleRequests : List<Observable<Muscle>> = exercise.allMuscles.map(MuscleClient::getMuscle)

        Observable.zip(muscleRequests) { results: Array<Any> ->
            results.joinToString(", ") { result -> (result as Muscle).name }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { listText -> holder.muscleList.text = "$muscles $listText" },
                { holder.muscleList.text = "Muscle(s) not found." }
            )
    }

    class ExerciseViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView) {

        var exerciseName: TextView
        var categoryName: TextView
        var equipmentList: TextView
        var muscleList: TextView
        var image: ImageView

        init {
            exerciseName = itemLayoutView.findViewById(R.id.exercise_name)
            categoryName = itemLayoutView.findViewById(R.id.category_name)
            equipmentList = itemLayoutView.findViewById(R.id.equipment_list)
            muscleList = itemLayoutView.findViewById(R.id.muscle_list)
            image = itemLayoutView.findViewById(R.id.image)
        }
    }

    class ExerciseCardOnClickListener(val exercise : Exercise) : View.OnClickListener {

        override fun onClick(v: View?) {
            val intent = Intent(v!!.context, ExerciseInfoActivity::class.java).apply {
                putExtra(EXERCISE_ID, exercise.id)
            }
            startActivity(v!!.context, intent, null)
        }
    }
}