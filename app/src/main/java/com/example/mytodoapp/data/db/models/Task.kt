package com.example.mytodoapp.data.db.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "task_table")
@Parcelize
data class Task(
    val title: String,
    val description: String = "",
    var expireDate: Long = -1,
    val isStarred: Boolean = false,
    val isDone: Boolean = false,
    val category: String = "Inbox",
    val priority: Int = 0,
    val createdDate: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
) : Parcelable {

    val createdDateFormatted: String
        get() = SimpleDateFormat("dd MMM").format(Date(createdDate))
    val expireDateFormatted: String
        get() = SimpleDateFormat("dd MMM").format(Date(expireDate))
}