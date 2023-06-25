package nodo.crogers.exercisereminders;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.Instant;
import java.util.Optional;

public class PreferenceManager {

    private static final String PAUSED = "paused";
    private static final String START_HOUR = "start-hour";
    private static final String START_MINUTE = "start-minute";
    private static final String END_HOUR = "end-hour";
    private static final String END_MINUTE = "end-minute";
    private static String FREQUENCY = "frequency";

    private static final String NEXT_SCHEDULED_ALARM = "next_scheduled_alarm";
    private static PreferenceManager instance;
    private static SharedPreferences sharedPreferences;

    private PreferenceManager(Context context) {
        sharedPreferences = context
                .getSharedPreferences(
                        "nodo.crogers.exercisereminders.settings",
                        Context.MODE_PRIVATE);
    }

    public static PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context);
        }
        return instance;
    }

    public boolean isPaused() {
        return sharedPreferences.getBoolean(PAUSED, false);
    }

    public void togglePaused() {
        boolean isPaused = isPaused();
        sharedPreferences.edit().putBoolean(PAUSED, !isPaused).apply();
    }

    public void setFrequency(int minutes) {
        sharedPreferences.edit()
                .putInt(FREQUENCY, minutes)
                .apply();
    }

    public void setStartTime(int hour, int minute) {
        sharedPreferences.edit()
                .putInt(START_HOUR, hour)
                .putInt(START_MINUTE, minute)
                .apply();
    }

    public void setEndTime(int hour, int minute) {
        sharedPreferences.edit()
                .putInt(END_HOUR, hour)
                .putInt(END_MINUTE, minute)
                .apply();
    }

    public int frequency() {
        return sharedPreferences.getInt(FREQUENCY, 60);
    }

    public int startHour() {
        return sharedPreferences.getInt(START_HOUR, 8);
    }

    public int startMinute() {
        return sharedPreferences.getInt(START_MINUTE, 0);
    }

    public int endHour() {
        return sharedPreferences.getInt(END_HOUR, 20);
    }

    public int endMinute() {
        return sharedPreferences.getInt(END_MINUTE, 0);
    }

    public Optional<Long> nextScheduledAlarm() {
        if (sharedPreferences.contains(NEXT_SCHEDULED_ALARM)) {
            return Optional.of(sharedPreferences.getLong(NEXT_SCHEDULED_ALARM, 0));
        }
        return Optional.empty();
    }

    public void setNextScheduledAlarm(long epochMilli) {
        sharedPreferences.edit().putLong(NEXT_SCHEDULED_ALARM, epochMilli).apply();
    }
}
