package com.example.mytodoapp.ui.fragments.newTask

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.mytodoapp.R
import com.example.mytodoapp.databinding.FragmentTaskAddEditBinding
import com.example.mytodoapp.util.convertLongDateToDate
import com.example.mytodoapp.util.exhaustive
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.skydoves.powerspinner.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class TaskAddEditFragment : Fragment(R.layout.fragment_task_add_edit) {
    private val viewModel: TaskAddEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTaskAddEditBinding.bind(view)

        binding.apply {
            editTextTaskName.setText(viewModel.taskName)
            checkBoxImportant.isChecked = viewModel.taskImportance
            checkBoxImportant.jumpDrawablesToCurrentState()

            //test
            powerSpinner.apply {
                setSpinnerAdapter(IconSpinnerAdapter(this))
                setItems(
                    arrayListOf(
                        IconSpinnerItem(text = "Low", iconRes = R.drawable.ic_high_priority_purple),
                        IconSpinnerItem(
                            text = "Normal",
                            iconRes = R.drawable.ic_high_priority_green
                        ),
                        IconSpinnerItem(
                            text = "High",
                            iconRes = R.drawable.ic_high_priority_yellow
                        ),
                        IconSpinnerItem(
                            text = "Critical",
                            iconRes = R.drawable.ic_high_priority_red
                        )
                    )
                )
                lifecycleOwner = viewLifecycleOwner
                selectItemByIndex(0)
                setupPriorityButtonText(powerSpinner, viewModel.taskPriority)
                setOnSpinnerItemSelectedListener<IconSpinnerItem> { oldIndex, oldItem, newIndex, newText ->
                    viewModel.taskPriority = newIndex
                    setupPriorityButtonText(powerSpinner, viewModel.taskPriority)
                }
            }

            //Listeners
            editTextTaskName.addTextChangedListener {
                viewModel.taskName = it.toString()
            }
            editTextTaskDesc.addTextChangedListener {
                viewModel.taskDesc = it.toString()
            }
            checkBoxImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.taskImportance = isChecked
            }
            fabSaveTask.setOnClickListener {
                viewModel.onSaveClick()
            }
            viewModel.taskExpirationDate = System.currentTimeMillis()   //default date
            btnDateSelect.setOnClickListener {
                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build()
                datePicker.show(requireFragmentManager(), "datepickertest")
                datePicker.addOnPositiveButtonClickListener {
                    if (convertLongDateToDate(it).equals(convertLongDateToDate(System.currentTimeMillis())))  //today
                        btnDateSelect.text = "Today"
                    else
                        btnDateSelect.text = convertLongDateToDate(it)
                    viewModel.taskExpirationDate = it
                }
            }

        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect { event ->
                when (event) {
                    is TaskAddEditViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG)
                            .show()
                        //todo top of fab [setAnchorView(...)]
                    }
                    is TaskAddEditViewModel.AddEditTaskEvent.NavigateBackWithResult -> {
                        binding.editTextTaskName.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        view.findNavController().popBackStack()
                    }
                }.exhaustive
            }
        }
    }

    private fun setupPriorityButtonText(spinner: PowerSpinnerView, priority: Int) {
        when (priority) {
            0 -> {
                val spannable = SpannableString("Priority : Low")
                spannable.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.purple_200
                        )
                    ),
                    11, 14,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spinner.text = spannable
            }

            1 -> {
                val spannable = SpannableString("Priority : Normal")
                spannable.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green
                        )
                    ),
                    11, 17,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spinner.text = spannable
            }
            2 -> {
                val spannable = SpannableString("Priority : High")
                spannable.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.yellow
                        )
                    ),
                    11, 15,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spinner.text = spannable
            }
            3 -> {
                val spannable = SpannableString("Priority : Critical")
                spannable.setSpan(
                    ForegroundColorSpan(Color.RED),
                    11, 19,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spinner.text = spannable
            }

        }

    }
}