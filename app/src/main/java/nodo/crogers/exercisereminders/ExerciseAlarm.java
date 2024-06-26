package nodo.crogers.exercisereminders;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

import nodo.crogers.exercisereminders.database.ERDatabase;
import nodo.crogers.exercisereminders.database.entities.Exercise;
import nodo.crogers.exercisereminders.database.daos.ExerciseDao;
import nodo.crogers.exercisereminders.ui.alarms.AlarmsViewModel;

// TODO - refactor: extract scheduling, improve handling of LiveData
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
            showNotification(context);
        }
        scheduleNext(context);
    }

    public static void showNotification(Context context) {
        ExerciseDao dao = ERDatabase.getInstance(context)
                .exerciseDao();

        ERDatabase.executorService.execute(() -> {
            List<Exercise> exercises = dao.getEligible();
            if (exercises.isEmpty()) {
                return;
            }

            Exercise exercise = exercises.get(random.nextInt(exercises.size()));
            displayNotification(context, exercise);

            dao.incrementCount(exercise);
        });


    }

    private static void displayNotification(Context context, Exercise exercise) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder notificationBuilder =
                new Notification.Builder(context, MainActivity.NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setColor(context.getResources().getColor(R.color.bleu_de_france2))
                        .setContentTitle(context.getString(R.string.exercise))
                        .setContentText(exercise.name())
                        .setGroup(NOTIFICATION_GROUP_ID)
                        .setAutoCancel(false);
        Notification.Builder summaryNotificationBuilder =
                new Notification.Builder(context, MainActivity.NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setColor(context.getResources().getColor(R.color.bleu_de_france2))
                        .setGroup(NOTIFICATION_GROUP_ID)
                        .setGroupSummary(true)
                        .setAutoCancel(false);
        int notificationId = (int) System.currentTimeMillis();
        int summaryId = 0;
        notificationManager.notify(notificationId, notificationBuilder.build());
        notificationManager.notify(summaryId, summaryNotificationBuilder.build());
    }

    public static void scheduleNext(Context context) {
        if (!hasPermission(context)) {
            return;
        }
        // TODO - base the next time on the time the notification _should_ have fone off? (as opposed to when in the window it did go off).
        PreferenceManager preferenceManager = PreferenceManager.getInstance(context);
        if (preferenceManager.getEnabledDays().stream().noneMatch(Boolean::booleanValue)) {
            return;
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent next = new Intent(context, ExerciseAlarm.class);
        int requestCode = 0;
        long time = nextTime(context);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                next,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextTime(context),
                pendingIntent);
        preferenceManager.setNextScheduledAlarm(time);
        AlarmsViewModel.nextScheduledAlarm.setValue(time);

    }

    public static boolean hasPermission(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return alarmManager.canScheduleExactAlarms();
        }
        return true;
    }

    public static void scheduleIfUnscheduled(Context context) {
        long nextScheduled = PreferenceManager.getInstance(context).nextScheduledAlarm().orElse(0L);
        if (nextScheduled < System.currentTimeMillis()) {
            scheduleNext(context);
        }
    }

    private static long nextTime(Context context) {
        PreferenceManager preferenceManager = PreferenceManager.getInstance(context);

        int startHour = preferenceManager.startHour();
        int startMinute = preferenceManager.startMinute();
        OffsetDateTime startTime = getTodayAt(startHour, startMinute);
        OffsetDateTime endTime =
                getTodayAt(preferenceManager.endHour(), preferenceManager.endMinute());
        OffsetDateTime now = OffsetDateTime.now(clock);

        // Monday is 0; Sunday is 6
        int currentDayOfWeek = now.getDayOfWeek().getValue() - 1;
        List<Boolean> enabledDays = preferenceManager.getEnabledDays();
        boolean todayIsEnabled = enabledDays.get(currentDayOfWeek);

        OffsetDateTime next;
        if (todayIsEnabled && now.isBefore(startTime)) {
            next = now
                    .withHour(startHour)
                    .withMinute(startMinute);
        }
        else if (!todayIsEnabled || now.isAfter(endTime)) {
            next = now
                    .plusDays(daysUntilEnabled(currentDayOfWeek, enabledDays))
                    .withHour(startHour)
                    .withMinute(startMinute);
        }
        else {
            int frequency = preferenceManager.frequency();
            next = now.plusMinutes(frequency);
        }

        next = next.truncatedTo(ChronoUnit.MINUTES);

        return next
                .toInstant()
                .toEpochMilli();

    }

    private static int daysUntilEnabled(int dayOfWeek, List<Boolean> enabledDays) {
        for (int i = 1; i <= 7; i++) {
            if (enabledDays.get((dayOfWeek + i) % 7)) {
                return i;
            }
        }
        throw new IllegalArgumentException("At least one day must be enabled!");
    }

    private static OffsetDateTime getTodayAt(int hour, int minute) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        return now
                .withHour(hour)
                .withMinute(minute)
                .truncatedTo(ChronoUnit.MINUTES);
    }

}
