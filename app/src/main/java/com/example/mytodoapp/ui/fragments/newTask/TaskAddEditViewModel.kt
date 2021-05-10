package com.example.mytodoapp.ui.fragments.newTask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytodoapp.R
import com.example.mytodoapp.data.db.models.Task
import com.example.mytodoapp.repository.TaskRepository
import com.example.mytodoapp.ui.ADD_TASK_RESULT_OK
import com.example.mytodoapp.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

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
    var taskDesc = state.get<String>("taskName") ?: task?.description ?: ""
        set(value) {
            field = value
            state.set("taskDesc", value)
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.isStarred ?: false
        set(value) {
            field = value
            state.set("taskImportance", value)
        }
    var taskExpirationDate = state.get<Long>("taskExpirationDate") ?: task?.expireDate ?: 0
        set(value) {
            field = value
            state.set("taskExpirationDate", value)
        }

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (taskName.isBlank()) {
            showInvalidInputMessage("Name cannot be empty")
            return
        }

        if (task != null) {
            val updatedTask =
                task.copy(
                    title = taskName,
                    isStarred = taskImportance,
                    description = taskDesc,
                    expireDate = taskExpirationDate
                )
            updateTask(updatedTask)
        } else {
            val newTask = Task(
                title = taskName,
                isStarred = taskImportance,
                description = taskDesc,
                expireDate = taskExpirationDate
            )
            createTask(newTask)
        }
    }

    private fun createTask(task: Task) = viewModelScope.launch {
        taskRepository.insert(task)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateTask(task: Task) = viewModelScope.launch {
        taskRepository.update(task)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(text))
    }

    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()
    }
}