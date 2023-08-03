package nodo.crogers.exercisereminders.ui.alarms;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.List;

import nodo.crogers.exercisereminders.PreferenceManager;
import nodo.crogers.exercisereminders.R;

public class DayPicker {

    public static void initialize(View dayPickerLayout, PreferenceManager preferenceManager) {
        List<Boolean> enabledDays = preferenceManager.getEnabledDays();
        ToggleButton sunday = dayPickerLayout.findViewById(R.id.t0);
        ToggleButton monday = dayPickerLayout.findViewById(R.id.t1);
        ToggleButton tuesday = dayPickerLayout.findViewById(R.id.t2);
        ToggleButton wednesday = dayPickerLayout.findViewById(R.id.t3);
        ToggleButton thursday = dayPickerLayout.findViewById(R.id.t4);
        ToggleButton friday = dayPickerLayout.findViewById(R.id.t5);
        ToggleButton satuday = dayPickerLayout.findViewById(R.id.t6);

        sunday.setChecked(enabledDays.get(0));
        monday.setChecked(enabledDays.get(1));
        tuesday.setChecked(enabledDays.get(2));
        wednesday.setChecked(enabledDays.get(3));
        thursday.setChecked(enabledDays.get(4));
        friday.setChecked(enabledDays.get(5));
        satuday.setChecked(enabledDays.get(6));

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> {
            int id = buttonView.getId();
            if (id == R.id.t0) {
                enabledDays.set(0, isChecked);
            } else if (id == R.id.t1){
                enabledDays.set(1, isChecked);
            } else if (id == R.id.t2){
                enabledDays.set(2, isChecked);
            } else if (id == R.id.t3){
                enabledDays.set(3, isChecked);
            } else if (id == R.id.t4){
                enabledDays.set(4, isChecked);
            } else if (id == R.id.t5){
                enabledDays.set(5, isChecked);
            } else if (id == R.id.t6){
                enabledDays.set(6, isChecked);
            }
            preferenceManager.setEnabledDays(enabledDays);
        };

        sunday.setOnCheckedChangeListener(onCheckedChangeListener);
        monday.setOnCheckedChangeListener(onCheckedChangeListener);
        tuesday.setOnCheckedChangeListener(onCheckedChangeListener);
        wednesday.setOnCheckedChangeListener(onCheckedChangeListener);
        thursday.setOnCheckedChangeListener(onCheckedChangeListener);
        friday.setOnCheckedChangeListener(onCheckedChangeListener);
        satuday.setOnCheckedChangeListener(onCheckedChangeListener);

    }
}
