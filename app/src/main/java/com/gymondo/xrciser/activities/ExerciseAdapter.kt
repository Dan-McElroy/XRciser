package com.gymondo.xrciser.activities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gymondo.xrciser.R
import com.gymondo.xrciser.data.Exercise

class ExerciseAdapter(private var exerciseList: List<Exercise>, private val context: Context)
    : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.exercise_card, parent, false))
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = exerciseList.get(position)

        holder.nameTextView.text = exercise.name
    }

    class ViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView) {

        lateinit var nameTextView: TextView

        init {
            nameTextView = itemLayoutView.findViewById(R.id.name)
        }
    }

}