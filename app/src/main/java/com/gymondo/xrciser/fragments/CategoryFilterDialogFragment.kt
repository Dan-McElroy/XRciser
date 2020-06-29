package com.gymondo.xrciser.fragments

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.gymondo.xrciser.R
import com.gymondo.xrciser.client.CategoryClient

const val SELECTED_CATEGORY_ID = "selected_category"

class CategoryFilterDialogFragment : BottomSheetDialogFragment() {

    lateinit var filterListener : Filterable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_filter_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val radioGroup = view.findViewById<RadioGroup>(R.id.filter_group)

        view.findViewById<RadioButton>(R.id.filter_option_reset)
            .setOnClickListener(FilterOnClickListener(null, filterListener))

        CategoryClient.getAllCached()
            .sortedBy { category -> category.name }
            .forEach { category ->
            val radioButton =  RadioButton.inflate(context,
                R.layout.category_filter_option, null) as RadioButton
            radioButton.id = category.id
            radioButton.text = category.name
            radioButton.setOnClickListener(FilterOnClickListener(category.id, filterListener))
            radioGroup.addView(radioButton)
        }
        var selectedCategoryId = arguments?.get(SELECTED_CATEGORY_ID) as? Int
            ?: R.id.filter_option_reset
        radioGroup.check(selectedCategoryId)

    }

    interface Filterable {
        fun onFilterChanged(categoryId: Int?)
    }

    private class FilterOnClickListener(val categoryId : Int?, val filterListener: Filterable) : View.OnClickListener {

        override fun onClick(v: View?) {
            filterListener.onFilterChanged(categoryId)
        }
    }

    companion object {

        fun newInstance(selectedId: Int?): CategoryFilterDialogFragment =
            CategoryFilterDialogFragment().apply {
                arguments = Bundle().apply {
                    if (selectedId is Int) {
                        putInt(SELECTED_CATEGORY_ID, selectedId)
                    }
                }
            }
    }
}