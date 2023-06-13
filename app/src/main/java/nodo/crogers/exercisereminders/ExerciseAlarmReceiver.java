package nodo.crogers.exercisereminders;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import java.util.Random;

import nodo.crogers.exercisereminders.ui.home.ExercisesViewModel;

public class ExerciseAlarmReceiver extends BroadcastReceiver {
    private static final Random random = new Random();

    @Override
    public void onReceive(Context context, Intent intent) {
        scheduleNext(context);
        showNotification(context);
    }

    public static void showNotification(Context context) {
        int numExercises = ExercisesViewModel.EXERCISES.length;
        int randomIndex = random.nextInt(numExercises);
        String exercise = ExercisesViewModel.EXERCISES[randomIndex];
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, MainActivity.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round) // notification icon
                .setContentTitle("Exercise!") // title for notification
                .setContentText(exercise)// message for notification
                .setAutoCancel(false); // clear notification after click
        Intent notificationIntent = new Intent(context, ExerciseAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getActivity(
                context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.setContentIntent(pi);
        notificationManager.notify(0, notificationBuilder.build());
    }

    public static void scheduleNext(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent next = new Intent(context, ExerciseAlarmReceiver.class);
        next.setAction("exercise");
        next.putExtra("key", "value");
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, next, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setWindow(
                AlarmManager.RTC_WAKEUP,
                // TODO - set this for the top of the next hour
                System.currentTimeMillis() + 1000,
                10 * 60 * 1000,
                pendingIntent);
    }
}
