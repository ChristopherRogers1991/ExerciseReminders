package nodo.crogers.exercisereminders.ui.settings;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Locale;
import java.util.Objects;

import nodo.crogers.exercisereminders.ExerciseAlarm;
import nodo.crogers.exercisereminders.PreferenceManager;
import nodo.crogers.exercisereminders.R;
import nodo.crogers.exercisereminders.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Context context = this.getContext();
        PreferenceManager preferenceManager = PreferenceManager.getInstance(context);
        Button pauseButton = root.findViewById(R.id.pauseButton);
        Runnable setPauseButtonText = () -> pauseButton.setText(preferenceManager.isPaused() ? "Resume" : "Pause");
        setPauseButtonText.run();
        pauseButton.setOnClickListener(_view -> {
            preferenceManager.togglePaused();
            setPauseButtonText.run();
            if (!preferenceManager.isPaused()) {
                ExerciseAlarm.scheduleNext(Objects.requireNonNull(context));
            }
        });

        Button startTimeButton = root.findViewById(R.id.startTimeButton);
        Runnable setStartButtonText = () -> startTimeButton.setText(startTimeButtonText(preferenceManager));
        setStartButtonText.run();
        startTimeButton.setOnClickListener(_view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    preferenceManager.setStartTime(hourOfDay, minute);
                    setStartButtonText.run();
                    ExerciseAlarm.scheduleNext(Objects.requireNonNull(context));
                }
            }, 8, 0, false);
            timePickerDialog.show();
        });

        Button endTimeButton = root.findViewById(R.id.endTimeButton);
        Runnable setEndButtonText = () -> endTimeButton.setText(endTimeButtonText(preferenceManager));
        setEndButtonText.run();
        endTimeButton.setOnClickListener(_view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    preferenceManager.setEndTime(hourOfDay, minute);
                    setEndButtonText.run();
                }
            }, 8, 0, false);
            timePickerDialog.show();
        });

        return root;
    }

    private String startTimeButtonText(PreferenceManager preferenceManager) {
        return timeText(preferenceManager.startHour(), preferenceManager.startMinute());
    }

    private String endTimeButtonText(PreferenceManager preferenceManager) {
        return timeText(preferenceManager.endHour(), preferenceManager.endMinute());
    }

    private String timeText(int hour, int minute) {
        String am_pm = hour <= 12 ? "AM" : "PM";
        return String.format(Locale.getDefault(), "%d:%02d %s", hour % 12, minute, am_pm);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}