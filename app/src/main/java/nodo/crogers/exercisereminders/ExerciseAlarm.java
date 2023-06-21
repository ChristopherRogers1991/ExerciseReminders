package nodo.crogers.exercisereminders;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import nodo.crogers.exercisereminders.ui.home.ExercisesViewModel;

public class ExerciseAlarm extends BroadcastReceiver {
    private static final String NOTIFICATION_GROUP_ID =
            "nodo.crogers.exerciseremoinders.NOTIFICATIONS";
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
                && !now.isAfter(endTime)) {
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
        Notification.Builder notificationBuilder =
                new Notification.Builder(context, MainActivity.NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("Exercise!")
                        .setContentText(exercise)
                        .setGroup(NOTIFICATION_GROUP_ID)
                        .setAutoCancel(false);
        Notification.Builder summaryNotificationBuilder =
                new Notification.Builder(context, MainActivity.NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setGroup(NOTIFICATION_GROUP_ID)
                        .setGroupSummary(true)
                        .setAutoCancel(false);
        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
        notificationManager.notify(0, summaryNotificationBuilder.build());
    }

    public static void scheduleNext(Context context) {
        // TODO - clear any already scheduled notifications
        // TODO - base the next time on the time the notification _should_ have fone off? (as opposed to when in the window it did go off).
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent next = new Intent(context, ExerciseAlarm.class);
        next.setAction("exercise");
        next.putExtra("key", "value");
        int requestCode = 0;
        int windowLengthInMs = 10 * 60 * 1000;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, next, PendingIntent.FLAG_IMMUTABLE);
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
            int frequency = preferenceManager.frequency();
            next = now
                    .plusMinutes(frequency);
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
