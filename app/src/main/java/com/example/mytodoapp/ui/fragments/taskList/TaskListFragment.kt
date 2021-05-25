package com.example.mytodoapp.ui.fragments.taskList

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodoapp.util.onQueryTextChanged
import com.example.mytodoapp.util.startAnimation
import com.example.mytodoapp.R
import com.example.mytodoapp.data.SortOrder
import com.example.mytodoapp.databinding.FragmentTaskListBinding
import com.example.mytodoapp.data.db.models.Task
import com.example.mytodoapp.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskListFragment : Fragment(R.layout.fragment_task_list),
    TaskListAdapter.OnItemPressListener {
    private val viewModel: TaskListViewModel by viewModels()
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTaskListBinding.bind(view)
        val taskAdapter = TaskListAdapter(requireContext(), this)

        binding.apply {
            recyclerViewTasks.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
//                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = taskAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwiped(task)
                }
            }).attachToRecyclerView(recyclerViewTasks)

            fabAddTask.setOnClickListener {
                viewModel.onAddNewTaskClick()
            }
        }
        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            taskAdapter.submitList(it)
        }

        //events
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tasksEvent.collect { event ->
                when (event) {
                    is TaskListViewModel.TasksEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar.make(requireView(), "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onUndoDeleteClick(event.task)
                            }.show()
                    }
                    is TaskListViewModel.TasksEvent.NavigateToAddTaskScreen -> {
                        startExplosionAnimation(binding, view)
//                        val action =
//                            TaskListFragmentDirections.actionTaskListFragmentToTaskAddEditFragment(
//                                null,
//                                "New Task"
//                            )
//                        view.findNavController().navigate(action)
                    }
                    is TaskListViewModel.TasksEvent.NavigateToEditTaskScreen -> {
                        val action =
                            TaskListFragmentDirections.actionTaskListFragmentToTaskAddEditFragment(
                                event.task,
                                "Edit Task"
                            )
                        view.findNavController().navigate(action)
                    }
                    is TaskListViewModel.TasksEvent.ShowTaskSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    TaskListViewModel.TasksEvent.NavigateToDeleteAllDoneScreen -> {
                        val action =
                            TaskListFragmentDirections.actionGlobalDeleteAllDoneDialogFragment()
                        view.findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }

        setHasOptionsMenu(true)

        //test
//        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.circle_explosion_anim).apply {
//            duration = 700
//            interpolator = AccelerateDecelerateInterpolator()
//        }
//
//        binding.fabAddTask.setOnClickListener {
//            binding.fabAddTask.isVisible = false
//            binding.circle.isVisible = true
//            binding.circle.startAnimation(animation) {
//                // display your fragment
//                binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_200))
//                binding.circle.isVisible = false
//            }
//        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_task_list, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }
        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_tasks).isChecked =
                viewModel.preferencesFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_expire_date -> {
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                viewModel.onHideCompletedClick(item.isChecked)
                true
            }
            R.id.action_delete_all_completed_tasks -> {
                viewModel.onDeleteAllDoneClick()
                true
            }
            R.id.settings -> {
                //todo
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(task: Task) {
        viewModel.onTaskSelected(task)
    }

    override fun onCheckBoxDoneClick(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckedChanged(task, isChecked)
    }

    override fun onCheckBoxImportantClick(task: Task, isChecked: Boolean) {
        viewModel.onTaskImportantCheckedChanged(task, isChecked)
    }


    private fun startExplosionAnimation(binding: FragmentTaskListBinding, view: View) {
        val animation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.circle_explosion_anim).apply {
                duration = 700
                interpolator = AccelerateDecelerateInterpolator()
            }

        binding.fabAddTask.isVisible = false
        binding.circle.isVisible = true
        binding.circle.startAnimation(animation) {
            // display your fragment
            binding.root.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.win8_blue
                )
            )
            binding.circle.isVisible = false

            val action =
                TaskListFragmentDirections.actionTaskListFragmentToTaskAddEditFragment(
                    null,
                    "New Task"
                )
            view.findNavController().navigate(action)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}