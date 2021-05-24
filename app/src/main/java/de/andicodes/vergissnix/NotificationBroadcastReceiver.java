package de.andicodes.vergissnix;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.concurrent.Executors;

import androidx.core.app.AlarmManagerCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import de.andicodes.vergissnix.data.AppDatabase;
import de.andicodes.vergissnix.data.Task;
import de.andicodes.vergissnix.data.TaskDao;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    private static final String ACTION_SHOW_NOTIFICATION = "de.andicodes.vergissnix.ACTION_SHOW_NOTIFICATION";
    private static final String EXTRA_TASK_ID = "de.andicodes.vergissnix.EXTRA_TASK_ID";
    private static final String NOTIFICATION_CHANNEL_ID = "de.andicodes.vergissnix.TASK_NOTIFICATION_CHANNEL";
    private static final String NOTIFICATION_GROUP_ID = "de.andicodes.vergissnix.NOTIFICATION_GROUP_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            setNotificationAlarms(context);
        } else if (ACTION_SHOW_NOTIFICATION.equals(intent.getAction())) {
            showNotification(context, intent.getLongExtra(EXTRA_TASK_ID, -1L));
        }
    }

    private void setNotificationAlarms(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        TaskDao taskDao = AppDatabase.getDatabase(context).taskDao();
        List<Task> tasks = taskDao.allTasks().getValue();
        if (tasks != null) {
            for (Task task : tasks) {
                Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
                intent.setAction(ACTION_SHOW_NOTIFICATION);
                intent.putExtra(EXTRA_TASK_ID, task.getId());

                PendingIntent broadcast = PendingIntent.getBroadcast(context, Long.hashCode(task.getId()), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC, task.getTime().toEpochSecond() * 1000, broadcast);
            }
        }
    }

    public static void setNotificationAlarm(Context context, Task task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (task.getTime() != null) {
            Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
            intent.setAction(ACTION_SHOW_NOTIFICATION);
            intent.putExtra(EXTRA_TASK_ID, task.getId());

            PendingIntent broadcast = PendingIntent.getBroadcast(context, Long.hashCode(task.getId()), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC, task.getTime().toEpochSecond() * 1000, broadcast);
        }
    }

    private void showNotification(Context context, long taskId) {
        TaskDao taskDao = AppDatabase.getDatabase(context).taskDao();
        Executors.newSingleThreadExecutor().execute(() -> {
            Task task = taskDao.getTask(taskId);
            if (task == null) {
                return;
            }

            Intent openMainActivity = new Intent(context, MainActivity.class);
            openMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent notificationClickIntent = PendingIntent.getActivity(context, Long.hashCode(task.getId()), openMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_access_time_24)
                    .setContentTitle(task.getText())
                    .setContentText(task.getTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)))
                    .setWhen(task.getTime().toEpochSecond() * 1000)
                    .setContentIntent(notificationClickIntent)
                    .setGroup(NOTIFICATION_GROUP_ID)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .build();

            NotificationManagerCompat.from(context).notify(Long.hashCode(task.getId()), notification);
        });
    }

    public static void createNotificationChannel(Context context) {
        String name = context.getString(R.string.notification_channel_name);
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel);
    }
}