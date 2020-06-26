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

// TODO: Customize parameter argument names
const val SELECTED_CATEGORY_ID = "selected_category"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    CategoryFilterDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class CategoryFilterDialogFragment : BottomSheetDialogFragment() {

    lateinit var filterListener : CategoryFilterListener

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

        CategoryClient.getAllCached().forEach { category ->
            val radioButton = RadioButton(context)
            radioButton.id = category.id
            radioButton.text = category.name
            radioButton.setOnClickListener(FilterOnClickListener(category.id, filterListener))
            radioGroup.addView(radioButton)
        }
        var selectedCategoryId = savedInstanceState?.get(SELECTED_CATEGORY_ID)
        if (selectedCategoryId is Int) {
            radioGroup.check(selectedCategoryId)
        }

    }

    interface CategoryFilterListener {
        fun onFilterChanged(categoryId: Int?)
    }

    private class FilterOnClickListener(val categoryId : Int?, val filterListener: CategoryFilterListener) : View.OnClickListener {

        override fun onClick(v: View?) {
            filterListener.onFilterChanged(categoryId)
        }
    }

    companion object {

        // TODO: Customize parameters
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