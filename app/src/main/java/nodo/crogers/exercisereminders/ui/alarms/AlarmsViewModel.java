package nodo.crogers.exercisereminders.ui.alarms;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import nodo.crogers.exercisereminders.PreferenceManager;

public class AlarmsViewModel extends AndroidViewModel {
    public static final MutableLiveData<Long> nextScheduledAlarm = new MutableLiveData<>(0L);

    public AlarmsViewModel(@NonNull Application application) {
        super(application);
    }

    LiveData<Long> getNextScheduleAlarm(Context context) {
        nextScheduledAlarm.setValue(PreferenceManager.getInstance(context)
                .nextScheduledAlarm().orElse(0L));
        return nextScheduledAlarm;
    }
}
