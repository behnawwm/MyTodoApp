package com.example.mytodoapp.ui.fragments.newTask

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.mytodoapp.R
import com.example.mytodoapp.databinding.FragmentTaskAddEditBinding
import com.example.mytodoapp.util.exhaustive
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_task_list.*
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*

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
            textViewDateCreated.isVisible = viewModel.task != null
            textViewDateCreated.text = "Expire date: ${viewModel.task?.expireDateFormatted}"

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
            btnDateAddEdit.setOnClickListener{
                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build()
                datePicker.show(requireFragmentManager(), "datepickertest")
                datePicker.addOnPositiveButtonClickListener {
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
}