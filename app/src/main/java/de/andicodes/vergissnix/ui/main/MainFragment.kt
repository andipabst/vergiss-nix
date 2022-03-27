package de.andicodes.vergissnix.ui.main

import android.content.DialogInterface
import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import de.andicodes.vergissnix.Notifications.cancelNotification
import de.andicodes.vergissnix.R
import de.andicodes.vergissnix.data.Task
import de.andicodes.vergissnix.ui.main.MainViewModel
import de.andicodes.vergissnix.ui.main.MainViewModel.TaskFilter.Companion.of
import de.andicodes.vergissnix.ui.main.TaskListAdapter.TaskViewHolder

class MainFragment : Fragment() {
    private var viewModel: MainViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val taskList: RecyclerView = view.findViewById(R.id.task_list)
        taskList.layoutManager = LinearLayoutManager(requireContext())

        val taskListAdapter = TaskListAdapter { task: Task? ->
            if (task != null) {
                val bundle = Bundle()
                bundle.putSerializable("task", task)
                findNavController().navigate(R.id.action_mainFragment_to_editTaskFragment, bundle)
            }
        }
        taskList.adapter = taskListAdapter

        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    // no drag and drop
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = taskListAdapter.getTask(viewHolder.adapterPosition)
                    if (task != null) {
                        viewModel!!.markTaskDone(task)
                        Snackbar.make(requireView(), R.string.taskDone, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo) { v: View? -> viewModel!!.markTaskNotDone(task) }
                            .show()
                        cancelNotification(requireContext(), task.id)
                    }
                }

                override fun onChildDrawOver(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    // just move the foreground view instead of the whole element -> background view is exposed
                    val foregroundView = (viewHolder as TaskViewHolder).foregroundView
                    getDefaultUIUtil().onDrawOver(
                        c,
                        recyclerView,
                        foregroundView,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    // just move the foreground view instead of the whole element -> background view is exposed
                    val foregroundView = (viewHolder as TaskViewHolder).foregroundView
                    getDefaultUIUtil().clearView(foregroundView)
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    // just move the foreground view instead of the whole element -> background view is exposed
                    val foregroundView = (viewHolder as TaskViewHolder).foregroundView
                    getDefaultUIUtil().onDraw(
                        c,
                        recyclerView,
                        foregroundView,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(taskList)
        viewModel!!.currentTasks()
            .observe(viewLifecycleOwner) { tasks: List<Task> -> taskListAdapter.replaceTasks(tasks) }
        val addTaskButton: ExtendedFloatingActionButton = view.findViewById(R.id.add_task)
        addTaskButton.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_editTaskFragment) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private val activeFilterPosition: Int
        get() {
            val filterValue = viewModel!!.getFilter()
            return filterValue?.position ?: 1
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.filter_button) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.filter)
                .setSingleChoiceItems(
                    R.array.filter_items, activeFilterPosition
                ) { dialog: DialogInterface, filterPosition: Int ->
                    viewModel!!.setFilter(of(filterPosition))
                    dialog.dismiss()
                }
                .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}