package com.example.mytodoapp.data.db.dao

import androidx.room.*
import com.example.mytodoapp.data.SortOrder
import com.example.mytodoapp.data.db.models.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    fun getTasks(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
        when (sortOrder) {
            SortOrder.BY_DATE -> getTasksSortedByExpirationDate(query, hideCompleted)
            SortOrder.BY_NAME -> getTasksSortedByName(query, hideCompleted)
        }

    @Query("SELECT * FROM task_table WHERE (isDone != :hideCompleted OR isDone = 0) AND title LIKE '%' || :searchQuery || '%' ORDER BY isStarred DESC, title")
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE (isDone != :hideCompleted OR isDone = 0) AND title LIKE '%' || :searchQuery || '%' ORDER BY isStarred DESC, expireDate")
    fun getTasksSortedByExpirationDate(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("DELETE FROM task_table WHERE isDone = 1")
    suspend fun deleteDoneTasks()
}