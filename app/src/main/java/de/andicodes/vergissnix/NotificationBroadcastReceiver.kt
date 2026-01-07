package de.andicodes.vergissnix

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.material3.ExperimentalMaterial3Api
import de.andicodes.vergissnix.data.AppDatabase.Companion.getDatabase
import de.andicodes.vergissnix.data.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

@ExperimentalMaterial3Api
class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action || Intent.ACTION_MY_PACKAGE_REPLACED == intent.action) {
            setNotificationAlarms(context)
        } else if (ACTION_SHOW_NOTIFICATION == intent.action) {
            Notifications.showNotification(
                context,
                intent.getLongExtra(Notifications.EXTRA_TASK_ID, -1L)
            )
        } else if (ACTION_MARK_AS_DONE == intent.action) {
            markAsDone(context, intent.getLongExtra(Notifications.EXTRA_TASK_ID, -1L))
        }
    }

    private fun setNotificationAlarms(context: Context) {
        val taskDao = getDatabase(context)!!.taskDao()
        CoroutineScope(Dispatchers.IO).launch {
            val tasks = taskDao.allTasks().firstOrNull()
            if (tasks != null) {
                for (task in tasks) {
                    setNotificationAlarm(context, task)
                }
            }
        }
    }

    private fun markAsDone(context: Context, taskId: Long) {
        val taskDao = getDatabase(context)!!.taskDao()
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.getTask(taskId)?.let { task ->
                if (task.timeDone == null) {
                    task.timeDone = ZonedDateTime.now()
                }
                taskDao.saveTask(task)
            }
            Notifications.cancelNotification(context, taskId)
        }
    }

    companion object {
        private const val ACTION_SHOW_NOTIFICATION =
            "de.andicodes.vergissnix.ACTION_SHOW_NOTIFICATION"
        const val ACTION_MARK_AS_DONE = "de.andicodes.vergissnix.ACTION_MARK_AS_DONE"

        fun setNotificationAlarm(context: Context, task: Task) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (task.time != null) {
                val intent = Intent(context, NotificationBroadcastReceiver::class.java)
                intent.action = ACTION_SHOW_NOTIFICATION
                intent.putExtra(Notifications.EXTRA_TASK_ID, task.id)
                val broadcast = PendingIntent.getBroadcast(
                    context,
                    java.lang.Long.hashCode(task.id),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager[AlarmManager.RTC, task.time!!.toEpochSecond() * 1000] = broadcast
            }
        }
    }
}