package de.andicodes.vergissnix;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Executors;

import de.andicodes.vergissnix.data.AppDatabase;
import de.andicodes.vergissnix.data.Task;
import de.andicodes.vergissnix.data.TaskDao;

import static de.andicodes.vergissnix.Notifications.EXTRA_TASK_ID;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION_SHOW_NOTIFICATION = "de.andicodes.vergissnix.ACTION_SHOW_NOTIFICATION";
    static final String ACTION_MARK_AS_DONE = "de.andicodes.vergissnix.ACTION_MARK_AS_DONE";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
                Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            setNotificationAlarms(context);
        } else if (ACTION_SHOW_NOTIFICATION.equals(intent.getAction())) {
            Notifications.showNotification(context, intent.getLongExtra(EXTRA_TASK_ID, -1L));
        } else if (ACTION_MARK_AS_DONE.equals(intent.getAction())) {
            markAsDone(context, intent.getLongExtra(EXTRA_TASK_ID, -1L));
        }
    }

    private void setNotificationAlarms(Context context) {
        TaskDao taskDao = AppDatabase.getDatabase(context).taskDao();
        List<Task> tasks = taskDao.allTasks().getValue();
        if (tasks != null) {
            for (Task task : tasks) {
                setNotificationAlarm(context, task);
            }
        }
    }

    public static void setNotificationAlarm(Context context, Task task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (task.getTime() != null) {
            Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
            intent.setAction(ACTION_SHOW_NOTIFICATION);
            intent.putExtra(EXTRA_TASK_ID, task.getId());

            PendingIntent broadcast = PendingIntent.getBroadcast(context, Long.hashCode(task.getId()), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.set(AlarmManager.RTC, task.getTime().toEpochSecond() * 1000, broadcast);
        }
    }

    private void markAsDone(Context context, long taskId) {
        TaskDao taskDao = AppDatabase.getDatabase(context).taskDao();
        Executors.newSingleThreadExecutor().execute(() -> {
            Task task = taskDao.getTask(taskId);
            if (task == null) {
                return;
            }

            if (task.getTimeDone() == null) {
                task.setTimeDone(ZonedDateTime.now());
            }
            taskDao.saveTask(task);
            Notifications.cancelNotification(context, taskId);
        });
    }
}