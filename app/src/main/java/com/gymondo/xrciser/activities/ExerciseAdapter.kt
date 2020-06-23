package com.gymondo.xrciser.activities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gymondo.xrciser.R
import com.gymondo.xrciser.client.CategoryClient
import com.gymondo.xrciser.client.EquipmentClient
import com.gymondo.xrciser.data.Equipment
import com.gymondo.xrciser.data.Exercise
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.stream.Collectors.toList

class ExerciseAdapter(private var exerciseList: List<Exercise>, private val context: Context)
    : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.exercise_card, parent, false))
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val exercise = exerciseList[position]

        holder.exerciseName.text = exercise.name

        CategoryClient.getCategory(exercise.category)
            .subscribe(
                { response -> holder.categoryName.text = response.name},
                { holder.categoryName.text = "Category not found"})
    }

    class ViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView) {

        lateinit var exerciseName: TextView
        lateinit var categoryName: TextView
        lateinit var equipmentList: TextView

        init {
            exerciseName = itemLayoutView.findViewById(R.id.exercise_name)
            categoryName = itemLayoutView.findViewById(R.id.category_name)
            equipmentList = itemLayoutView.findViewById(R.id.equipment_list)
        }
    }

}