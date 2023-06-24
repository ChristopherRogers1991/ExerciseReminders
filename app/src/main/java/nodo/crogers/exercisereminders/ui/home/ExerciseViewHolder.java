package nodo.crogers.exercisereminders.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import nodo.crogers.exercisereminders.R;
import nodo.crogers.exercisereminders.database.ERDatabase;
import nodo.crogers.exercisereminders.database.Exercise;

public class ExerciseViewHolder extends RecyclerView.ViewHolder {
    private final TextView exerciseTextView;
    private final CheckBox enabledCheckBox;
    private final View itemView;

    public ExerciseViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        exerciseTextView = itemView.findViewById(R.id.recyclerView_text);
        enabledCheckBox = itemView.findViewById(R.id.recyclerView_checkBox);
    }

    public void bind(Exercise exercise) {
        enabledCheckBox.setOnCheckedChangeListener(null);

        exerciseTextView.setText(exercise.name());
        enabledCheckBox.setChecked(exercise.enabled() == 1);
        enabledCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ERDatabase.executorService.execute(() -> ERDatabase.getInstance(itemView.getContext())
                        .exerciseDao()
                        .enable(exercise));
            } else {
                ERDatabase.executorService.execute(() -> ERDatabase.getInstance(itemView.getContext())
                        .exerciseDao()
                        .disable(exercise));
            }
        });
    }

    public static ExerciseViewHolder create(ViewGroup parent) {
        boolean attachToRoot = false;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, attachToRoot);
        return new ExerciseViewHolder(view);
    }
}
