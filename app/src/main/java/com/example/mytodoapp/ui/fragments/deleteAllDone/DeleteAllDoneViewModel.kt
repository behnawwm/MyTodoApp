package com.codinginflow.mvvmtodo.ui.deleteallcompleted

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.mytodoapp.di.ApplicationScope
import com.example.mytodoapp.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAllDoneViewModel @ViewModelInject constructor(
    private val repository: TaskRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    fun onConfirmClick() = applicationScope.launch {
        repository.deleteDoneTasks()
    }
}