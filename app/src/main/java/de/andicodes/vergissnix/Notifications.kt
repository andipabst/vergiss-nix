package de.andicodes.vergissnix

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import de.andicodes.vergissnix.data.AppDatabase.Companion.getDatabase
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.concurrent.Executors

@ExperimentalMaterial3Api
@ExperimentalLayoutApi
object Notifications {
    const val EXTRA_TASK_ID = "de.andicodes.vergissnix.EXTRA_TASK_ID"
    private const val NOTIFICATION_CHANNEL_ID = "de.andicodes.vergissnix.TASK_NOTIFICATION_CHANNEL"
    private const val NOTIFICATION_GROUP_ID = "de.andicodes.vergissnix.NOTIFICATION_GROUP_ID"

    fun showNotification(context: Context, taskId: Long) {
        val taskDao = getDatabase(context)!!.taskDao()
        Executors.newSingleThreadExecutor().execute {
            val task = taskDao.getTask(taskId) ?: return@execute

            val openMainActivity = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(EXTRA_TASK_ID, task.id)
            }
            val notificationClickIntent = PendingIntent.getActivity(
                context,
                0,
                openMainActivity,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val markAsDone = Intent(context, NotificationBroadcastReceiver::class.java).apply {
                action = NotificationBroadcastReceiver.ACTION_MARK_AS_DONE
                data = Uri.parse("content://task/" + task.id)
                putExtra(EXTRA_TASK_ID, task.id)
            }
            val markAsDoneIntent = PendingIntent.getBroadcast(
                context,
                0,
                markAsDone,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
            )

            val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(task.text)
                .setContentText(task.time!!.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)))
                .addAction(0, context.getString(R.string.done), markAsDoneIntent)
                .setWhen(task.time!!.toEpochSecond() * 1000)
                .setContentIntent(notificationClickIntent)
                .setGroup(NOTIFICATION_GROUP_ID)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .build()

            NotificationManagerCompat.from(context).notify(task.id.toInt(), notification)
        }
    }

    @JvmStatic
    fun cancelNotification(context: Context, taskId: Long) {
        NotificationManagerCompat.from(context).cancel(taskId.toInt())
    }

    fun createNotificationChannel(context: Context) {
        val name = context.getString(R.string.notification_channel_name)
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
    }
}