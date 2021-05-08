package com.example.mytodoapp.ui.fragments.taskList

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.mytodoapp.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest


class TaskListViewModel @ViewModelInject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    private val tasksFlow = searchQuery.flatMapLatest {
        repository.getAllTasks(it)
    }

    val tasks = tasksFlow.asLiveData()
}