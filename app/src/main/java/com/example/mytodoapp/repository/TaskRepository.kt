package com.example.mytodoapp.repository

import com.example.mytodoapp.db.dao.TaskDao
import javax.inject.Inject

class TaskRepository @Inject constructor(
    val taskDao: TaskDao,

    ) {
    fun getAllTasks(query:String)= taskDao.getTasks(query)
}