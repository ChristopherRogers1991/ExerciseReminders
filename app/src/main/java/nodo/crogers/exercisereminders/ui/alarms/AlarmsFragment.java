package nodo.crogers.exercisereminders.ui.alarms;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Locale;
import java.util.Date;

import nodo.crogers.exercisereminders.ExerciseAlarm;
import nodo.crogers.exercisereminders.PreferenceManager;
import nodo.crogers.exercisereminders.R;
import nodo.crogers.exercisereminders.databinding.FragmentAlarmsBinding;

public class AlarmsFragment extends Fragment {

    private FragmentAlarmsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAlarmsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        AlarmsViewModel alarmsViewModel = new ViewModelProvider(this).get(AlarmsViewModel.class);
        Context context = this.requireContext();
        PreferenceManager preferenceManager = PreferenceManager.getInstance(context);
        Button pauseButton = root.findViewById(R.id.pauseButton);
        Runnable updatePauseButton = () -> {
            if (preferenceManager.isPaused()) {
                pauseButton.setText(R.string.resume);
                pauseButton.setBackgroundColor(context.getColor(R.color.florescent_cyan));
            } else {
                pauseButton.setText(R.string.pause);
                pauseButton.setBackgroundColor(context.getColor(R.color.bleu_de_france2));
            }
        };
        updatePauseButton.run();
        pauseButton.setOnClickListener(_view -> {
            preferenceManager.togglePaused();
            updatePauseButton.run();
            int message;
            if (!preferenceManager.isPaused()) {
                message = R.string.notifications_resumed;
                ExerciseAlarm.scheduleIfUnscheduled(context);
            } else {
                message = R.string.notifications_paused;
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        });

        Button startTimeButton = root.findViewById(R.id.startTimeButton);
        Runnable setStartButtonText = () -> startTimeButton.setText(startTimeButtonText(preferenceManager));
        setStartButtonText.run();
        startTimeButton.setOnClickListener(_view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.DialogTheme, (view, hourOfDay, minute) -> {
                preferenceManager.setStartTime(hourOfDay, minute);
                setStartButtonText.run();
                ExerciseAlarm.scheduleNext(context);
            }, 8, 0, false);
            timePickerDialog.show();
        });

        Button endTimeButton = root.findViewById(R.id.endTimeButton);
        Runnable setEndButtonText = () -> endTimeButton.setText(endTimeButtonText(preferenceManager));
        setEndButtonText.run();
        endTimeButton.setOnClickListener(_view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.DialogTheme, (view, hourOfDay, minute) -> {
                preferenceManager.setEndTime(hourOfDay, minute);
                setEndButtonText.run();
            }, 8, 0, false);
            timePickerDialog.show();
        });

        EditText frequencyInput = root.findViewById(R.id.frequencyInput);
        Runnable setFrequencyInputText = () -> frequencyInput.setText(
                String.format(Locale.getDefault(),"%d", preferenceManager.frequency()));
        setFrequencyInputText.run();
        frequencyInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                try {
                    int frequency = Integer.parseInt(frequencyInput.getText().toString());
                    if (frequency < 10) {
                        frequency = 10;
                        Toast.makeText(context, R.string.minimum_frequency, Toast.LENGTH_SHORT)
                                .show();
                    }
                    preferenceManager.setFrequency(frequency);
                    ExerciseAlarm.scheduleNext(context);
                } catch (Exception e) {
                    // Do nothing
                }
                setFrequencyInputText.run();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                if(imm.isAcceptingText()) {
                    imm.hideSoftInputFromWindow(container.findFocus().getWindowToken(), 0);
                }
                container.clearFocus();
                return true;
            }
            return false;
        });


        final TextView nextAlarmTime = root.findViewById(R.id.nextAlarmTime);
        final DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        alarmsViewModel.getNextScheduleAlarm(context).observe(getViewLifecycleOwner(), nextAlarm -> {
            Date nextTime = Date.from(Instant.ofEpochMilli(nextAlarm));
            nextAlarmTime.setText(dateFormat.format(nextTime));
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