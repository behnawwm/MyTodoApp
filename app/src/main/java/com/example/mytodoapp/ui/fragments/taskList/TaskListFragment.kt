package com.example.mytodoapp.ui.fragments.taskList

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mytodoapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListFragment : Fragment(R.layout.fragment_task_list) {
    private val viewModel: TaskListViewModel by viewModels()


}