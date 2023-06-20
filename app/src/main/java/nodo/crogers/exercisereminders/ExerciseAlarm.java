package nodo.crogers.exercisereminders;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import nodo.crogers.exercisereminders.ui.home.ExercisesViewModel;

public class ExerciseAlarm extends BroadcastReceiver {
    private static final Random random = new Random();
    private static final Clock clock = Clock.systemDefaultZone();

    @Override
    public void onReceive(Context context, Intent intent) {
        PreferenceManager preferenceManager = PreferenceManager.getInstance(context);
        OffsetDateTime startTime =
                getTodayAt(preferenceManager.startHour(), preferenceManager.startMinute());
        OffsetDateTime endTime =
                getTodayAt(preferenceManager.endHour(), preferenceManager.endMinute());
        OffsetDateTime now = OffsetDateTime.now(clock);
        if (!PreferenceManager.getInstance(context).isPaused()
                && !now.isBefore(startTime)
                && ! now.isAfter(endTime)) {
            scheduleNext(context);
            showNotification(context);
        }
    }

    public static void showNotification(Context context) {
        int numExercises = ExercisesViewModel.EXERCISES.length;
        int randomIndex = random.nextInt(numExercises);
        String exercise = ExercisesViewModel.EXERCISES[randomIndex];
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, MainActivity.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Exercise!")
                .setContentText(exercise)
                .setAutoCancel(false);
        Intent notificationIntent = new Intent(context, ExerciseAlarm.class);
        PendingIntent pi = PendingIntent.getActivity(
                context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.setContentIntent(pi);
        notificationManager.notify(0, notificationBuilder.build());
    }

    public static void scheduleNext(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent next = new Intent(context, ExerciseAlarm.class);
        next.setAction("exercise");
        next.putExtra("key", "value");
        int requestCode = 0;
        int windowLengthInMs = 10 * 60 * 1000;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, next, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setWindow(
                AlarmManager.RTC_WAKEUP,
                nextTime(context),
                windowLengthInMs,
                pendingIntent);
    }

    private static long nextTime(Context context) {
        PreferenceManager preferenceManager = PreferenceManager.getInstance(context);

        int startHour = preferenceManager.startHour();
        int startMinute = preferenceManager.startMinute();
        OffsetDateTime startTime = getTodayAt(startHour, startMinute);
        OffsetDateTime endTime =
                getTodayAt(preferenceManager.endHour(), preferenceManager.endMinute());
        OffsetDateTime now = OffsetDateTime.now(clock);
        int currentHour = now.get(ChronoField.HOUR_OF_DAY);

        OffsetDateTime next;
        if (now.isBefore(startTime)) {
            next = now
                    .withHour(startHour)
                    .withMinute(startMinute);
        }
        else if (now.isAfter(endTime)) {
            next = now
                    .plusDays(1)
                    .withHour(startHour)
                    .withMinute(startMinute);
        }
        else {
            next = now
                    .withHour(currentHour + 1)
                    .withMinute(startMinute);
        }

        next = next.truncatedTo(ChronoUnit.MINUTES);

        Log.d("SCHEDULE", "nextTime: " + next);

        return next
                .toInstant()
                .toEpochMilli();

    }

    private static OffsetDateTime getTodayAt(int hour, int minute) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        return now
                .withHour(hour)
                .withMinute(minute)
                .truncatedTo(ChronoUnit.MINUTES);
    }
}
