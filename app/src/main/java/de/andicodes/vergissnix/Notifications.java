package de.andicodes.vergissnix;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.Executors;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import de.andicodes.vergissnix.data.AppDatabase;
import de.andicodes.vergissnix.data.Task;
import de.andicodes.vergissnix.data.TaskDao;

import static de.andicodes.vergissnix.NotificationBroadcastReceiver.ACTION_MARK_AS_DONE;

public class Notifications {
    static final String EXTRA_TASK_ID = "de.andicodes.vergissnix.EXTRA_TASK_ID";
    private static final String NOTIFICATION_CHANNEL_ID = "de.andicodes.vergissnix.TASK_NOTIFICATION_CHANNEL";
    private static final String NOTIFICATION_GROUP_ID = "de.andicodes.vergissnix.NOTIFICATION_GROUP_ID";


    public static void showNotification(Context context, long taskId) {
        TaskDao taskDao = AppDatabase.getDatabase(context).taskDao();
        Executors.newSingleThreadExecutor().execute(() -> {
            Task task = taskDao.getTask(taskId);
            if (task == null) {
                return;
            }

            Intent openMainActivity = new Intent(context, MainActivity.class);
            openMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openMainActivity.putExtra(EXTRA_TASK_ID, task.getId());
            PendingIntent notificationClickIntent = PendingIntent.getActivity(context, 0, openMainActivity, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            Intent markAsDone = new Intent(context, NotificationBroadcastReceiver.class);
            markAsDone.setAction(ACTION_MARK_AS_DONE);
            markAsDone.putExtra(EXTRA_TASK_ID, task.getId());
            PendingIntent markAsDoneIntent = PendingIntent.getBroadcast(context, 0, markAsDone, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);

            Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(task.getText())
                    .setContentText(task.getTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)))
                    .addAction(0, "Erledigt", markAsDoneIntent)
                    .setWhen(task.getTime().toEpochSecond() * 1000)
                    .setContentIntent(notificationClickIntent)
                    .setGroup(NOTIFICATION_GROUP_ID)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .build();

            NotificationManagerCompat.from(context).notify((int) task.getId(), notification);
        });
    }

    public static void cancelNotification(Context context, long taskId) {
        NotificationManagerCompat.from(context).cancel((int) taskId);
    }

    public static void createNotificationChannel(Context context) {
        String name = context.getString(R.string.notification_channel_name);
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel);
    }
}
