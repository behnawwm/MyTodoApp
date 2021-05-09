package com.example.mytodoapp.ui.fragments.taskList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodoapp.databinding.ItemTaskBinding
import com.example.mytodoapp.data.db.models.Task

class TaskListAdapter(private val listener: OnItemPressListener) :
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
                        listener.onCheckBoxClick(task, checkBoxDone.isChecked)
                    }
                }
                checkBoxImportant.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onCheckBoxClick(task, checkBoxDone.isChecked)
                    }
                }
            }
        }

        fun bind(task: Task) {
            binding.apply {
                checkBoxDone.isChecked = task.isDone
                checkBoxImportant.isChecked = task.isStarred
                tvTitle.text = task.title
                tvDate.text = task.createdDateFormatted

                //crossed
                tvDate.paint.isStrikeThruText = task.isDone
                tvTitle.paint.isStrikeThruText = task.isDone

            }
        }
    }

    interface OnItemPressListener {
        fun onItemClick(task: Task)
        fun onCheckBoxClick(task: Task, isChecked: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem == newItem
    }
}