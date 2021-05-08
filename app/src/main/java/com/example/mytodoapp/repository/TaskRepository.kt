package com.example.mytodoapp.repository

import com.example.mytodoapp.data.SortOrder
import com.example.mytodoapp.data.db.dao.TaskDao
import com.example.mytodoapp.data.db.models.Task
import javax.inject.Inject

class TaskRepository @Inject constructor(
    val taskDao: TaskDao,

    ) {
    fun getTasks(query: String, sortOrder: SortOrder, hideCompleted: Boolean) =
        taskDao.getTasks(query, sortOrder, hideCompleted)

    suspend fun update(task: Task) {
        taskDao.update(task)
    }
}