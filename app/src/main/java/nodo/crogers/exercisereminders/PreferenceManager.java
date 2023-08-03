package nodo.crogers.exercisereminders;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nodo.crogers.exercisereminders.ui.alarms.DayPicker;

public class PreferenceManager {

    private static final String PAUSED = "paused";
    private static final String START_HOUR = "start-hour";
    private static final String START_MINUTE = "start-minute";
    private static final String END_HOUR = "end-hour";
    private static final String END_MINUTE = "end-minute";
    private static final String FREQUENCY = "frequency";

    private static final String DAYS = "days";

    private static final String NEXT_SCHEDULED_ALARM = "next-scheduled-alarm";

    private static final String TERMS_ACCEPTED = "terms-accepted";
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

    public boolean termsAccepted() {
        return sharedPreferences.getBoolean(TERMS_ACCEPTED, false);
    }

    public void setTermsAccepted(boolean accepted) {
        sharedPreferences.edit().putBoolean(TERMS_ACCEPTED, accepted).apply();
    }

    public void setEnabledDays(List<Boolean> days) {
        String serialized = days.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
        sharedPreferences.edit().putString(DAYS, serialized).apply();
    }

    public List<Boolean> getEnabledDays() {
        String serialized = sharedPreferences.getString(DAYS, "true,true,true,true,true,true,true");
        return Arrays.stream(serialized.split(","))
                .map(Boolean::parseBoolean)
                .collect(Collectors.toList());
    }

}
