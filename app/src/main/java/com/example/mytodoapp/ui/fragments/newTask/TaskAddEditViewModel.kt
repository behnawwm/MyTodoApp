package com.example.mytodoapp.ui.fragments.newTask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.mytodoapp.R
import com.example.mytodoapp.data.db.models.Task
import com.example.mytodoapp.repository.TaskRepository

class TaskAddEditViewModel @ViewModelInject constructor(
    private val taskRepository: TaskRepository,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: task?.title ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.isDone ?: false
        set(value) {
            field = value
            state.set("taskImportance", value)
        }

}