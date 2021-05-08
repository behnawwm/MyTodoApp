package com.example.mytodoapp.ui.fragments.taskList

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.mytodoapp.repository.TaskRepository


class TaskListViewModel @ViewModelInject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    fun getAllTasks() = repository.getAllTasks()
}