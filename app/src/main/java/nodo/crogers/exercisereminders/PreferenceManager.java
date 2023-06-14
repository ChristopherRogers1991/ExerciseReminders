package nodo.crogers.exercisereminders;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String PAUSED = "paused";
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
}
