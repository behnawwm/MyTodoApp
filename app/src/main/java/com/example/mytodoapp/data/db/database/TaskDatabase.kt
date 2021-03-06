package com.example.mytodoapp.data.db.database


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mytodoapp.data.db.dao.TaskDao
import com.example.mytodoapp.data.db.models.Task
import com.example.mytodoapp.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao()

            applicationScope.launch {
//                dao.insert(Task("Wash the dishes", "dsadasd"))
//                dao.insert(Task("Do the laundry", "wwwe"))
//                dao.insert(Task("Buy groceries", "hhhhh", isStarred = true))
//                dao.insert(Task("Call mom", "hhh", isDone = true))
//                dao.insert(Task("Visit grandma", "dsj;flk", isStarred = true, isDone = true))

            }
        }
    }
}