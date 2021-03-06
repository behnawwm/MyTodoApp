package com.example.mytodoapp.ui.fragments.taskList

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodoapp.R
import com.example.mytodoapp.databinding.ItemTaskBinding
import com.example.mytodoapp.data.db.models.Task
import com.example.mytodoapp.util.convertLongDateToDate

class TaskListAdapter(val context: Context, private val listener: OnItemPressListener) :
    ListAdapter<Task, TaskListAdapter.TaskListViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class TaskListViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onItemClick(task)
                    }
                }
                checkBoxDone.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onCheckBoxDoneClick(task, checkBoxDone.isChecked)
                    }
                }
            }
        }

        fun bind(task: Task) {
            binding.apply {
                checkBoxDone.isChecked = task.isDone
                tvTitle.text = task.title
                tvDate.text = task.expireDateFormatted
                if (!task.description.isBlank())
                    tvDesc.text = task.description
                else
                    tvDesc.visibility = View.GONE
                tvCategory.text = task.category //todo

                //date color
                if (convertLongDateToDate(System.currentTimeMillis()).equals(task.expireDateFormatted))
                    tvDate.setTextColor(ContextCompat.getColor(context, R.color.green))
                else if (System.currentTimeMillis() > task.expireDate)
                    tvDate.setTextColor(ContextCompat.getColor(context, R.color.red))
                else
                    tvDate.setTextColor(ContextCompat.getColor(context, R.color.DarkSlateBlue))

                //done
//                tvDate.paint.isStrikeThruText = task.isDone
                tvTitle.paint.isStrikeThruText = task.isDone
                if (task.isDone) {
//                    rlWhiteBg.alpha = 0.38f
                    rlWhiteBg.setBackgroundResource(R.drawable.item_task_rounded_background_gray)
                    tvDate.setTextColor(ContextCompat.getColor(context, R.color.gray))
                }
                else{
                    rlWhiteBg.setBackgroundResource(R.drawable.item_task_rounded_background_white)
                }

                //priority
                when (task.priority) {
                    0 -> backgroundPriority.setBackgroundResource(R.drawable.item_task_rounded_background_purple)
                    1 -> backgroundPriority.setBackgroundResource(R.drawable.item_task_rounded_background_green)
                    2 -> backgroundPriority.setBackgroundResource(R.drawable.item_task_rounded_background_yellow)
                    3 -> backgroundPriority.setBackgroundResource(R.drawable.item_task_rounded_background_red)
                }

            }
        }
    }

    interface OnItemPressListener {
        fun onItemClick(task: Task)
        fun onCheckBoxDoneClick(task: Task, isChecked: Boolean)
        fun onCheckBoxImportantClick(task: Task, isChecked: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem == newItem
    }
}