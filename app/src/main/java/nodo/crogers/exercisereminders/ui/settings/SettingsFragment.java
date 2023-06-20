package nodo.crogers.exercisereminders.ui.settings;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

        EditText frequencyInput = root.findViewById(R.id.frequencyInput);
        Runnable setFrequencyInputText = () -> frequencyInput.setText(
                Integer.toString(preferenceManager.frequency()));
        setFrequencyInputText.run();
        frequencyInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    int frequency = Integer.parseInt(frequencyInput.getText().toString());
                    if (frequency < 10) {
                        frequency = 10;
                        Toast.makeText(context, "Minimum: 10 minutes", Toast.LENGTH_SHORT)
                                .show();
                    }
                    preferenceManager.setFrequency(frequency);
                    setFrequencyInputText.run();
                    ExerciseAlarm.scheduleNext(Objects.requireNonNull(context));
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                    if(imm.isAcceptingText()) {
                        imm.hideSoftInputFromWindow(container.findFocus().getWindowToken(), 0);
                    }
                    container.clearFocus();
                    return true;
                }
                return false;
            }
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
        String am_pm = hour < 12 ? "AM" : "PM";
        if (hour == 0) {
            hour = 12;
        } else if (hour > 12) {
            hour %= 12;
        }
        return String.format(Locale.getDefault(), "%d:%02d %s", hour, minute, am_pm);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}