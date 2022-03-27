package de.andicodes.vergissnix.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.andicodes.vergissnix.R
import de.andicodes.vergissnix.data.Task
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

class TaskListAdapter(editTask: Consumer<Task>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    enum class TemporalGrouping {
        OVERDUE, TODAY, TOMORROW, THIS_WEEK, THIS_MONTH, LATER
    }

    private val tasks = ArrayList<ListItem>()
    private val taskListener: TaskListener

    fun replaceTasks(tasks: List<Task>) {
        val now = ZonedDateTime.now()
        val today = now.withHour(0).withMinute(0).withSecond(0).withNano(0)
        val tasksWithHeadings: List<ListItem> = tasks.stream()
            .collect(Collectors.groupingBy { task ->
                val time = task?.time
                if (time == null || time.isBefore(today)) {
                    return@groupingBy TemporalGrouping.OVERDUE
                } else if (time.isAfter(today) && time.isBefore(today.plusDays(1))) {
                    return@groupingBy TemporalGrouping.TODAY
                } else if (time.isAfter(today.plusDays(1)) && time.isBefore(today.plusDays(2))) {
                    return@groupingBy TemporalGrouping.TOMORROW
                } else if (time.isAfter(today.plusDays(2)) && time.isBefore(today.plusWeeks(1))) {
                    return@groupingBy TemporalGrouping.THIS_WEEK
                } else if (time.isAfter(today.plusWeeks(1)) && time.isBefore(today.plusMonths(1))) {
                    return@groupingBy TemporalGrouping.THIS_MONTH
                } else {
                    return@groupingBy TemporalGrouping.LATER
                }
            })
            .entries
            .stream()
            .sorted(java.util.Map.Entry.comparingByKey())
            .flatMap { (key, value) ->
                Stream.concat(
                    Stream.of(HeaderItem(key)),
                    value.stream().map { task -> TaskItem(task) })
            }
            .collect(Collectors.toList())

        this.tasks.clear()
        this.tasks.addAll(tasksWithHeadings)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ListItem.HEADER_TYPE) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.task_list_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.task_list_item, parent, false)
            TaskViewHolder(view, taskListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == ListItem.HEADER_TYPE) {
            val headerItem = tasks[position] as HeaderItem
            val headerViewHolder = holder as HeaderViewHolder
            headerViewHolder.setTemporalGrouping(headerItem.temporalGrouping)
        } else if (viewType == ListItem.TASK_TYPE) {
            val taskItem = tasks[position] as TaskItem
            val taskViewHolder = holder as TaskViewHolder
            taskViewHolder.setTask(taskItem.task)
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun getItemViewType(position: Int): Int {
        return tasks[position].type
    }

    fun getTask(adapterPosition: Int): Task? {
        val listItem = tasks[adapterPosition]
        return if (listItem is TaskItem) {
            listItem.task
        } else null
    }

    class TaskViewHolder(itemView: View, taskListener: TaskListener) :
        RecyclerView.ViewHolder(itemView) {

        val foregroundView: View = itemView.findViewById(R.id.task_item_foreground)
        private val taskName: TextView = itemView.findViewById(R.id.task_name)
        private val dueDateTime: TextView = itemView.findViewById(R.id.due_date_time)

        private var task: Task? = null

        fun setTask(task: Task) {
            this.task = task
            taskName.text = task.text
            if (task.time != null) {
                dueDateTime.visibility = View.VISIBLE
                dueDateTime.text =
                    task.time?.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
            } else {
                dueDateTime.visibility = View.GONE
            }
        }

        init {
            itemView.setOnClickListener { task?.let { taskListener.editTask(it) } }
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskName: TextView = itemView.findViewById(R.id.text)

        fun setTemporalGrouping(temporalGrouping: TemporalGrouping?) {
            when (temporalGrouping) {
                TemporalGrouping.OVERDUE -> taskName.setText(R.string.overdue)
                TemporalGrouping.TODAY -> taskName.setText(R.string.today)
                TemporalGrouping.TOMORROW -> taskName.setText(R.string.tomorrow)
                TemporalGrouping.THIS_WEEK -> taskName.setText(R.string.this_week)
                TemporalGrouping.THIS_MONTH -> taskName.setText(R.string.this_month)
                TemporalGrouping.LATER -> taskName.setText(R.string.later)
                else -> {
                    taskName.text = ""
                }
            }
        }
    }

    interface TaskListener {
        fun editTask(task: Task)
    }

    internal abstract class ListItem(val type: Int) {

        companion object {
            const val HEADER_TYPE = 1
            const val TASK_TYPE = 2
        }
    }

    internal class HeaderItem(val temporalGrouping: TemporalGrouping) : ListItem(HEADER_TYPE)
    internal class TaskItem(val task: Task) : ListItem(TASK_TYPE)

    init {
        taskListener = object : TaskListener {
            override fun editTask(task: Task) {
                editTask.accept(task)
            }
        }
    }
}