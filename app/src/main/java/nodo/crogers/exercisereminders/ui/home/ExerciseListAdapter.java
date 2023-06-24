package nodo.crogers.exercisereminders.ui.home;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import nodo.crogers.exercisereminders.database.Exercise;

public class ExerciseListAdapter extends ListAdapter<Exercise, ExerciseViewHolder> {
    protected ExerciseListAdapter(@NonNull DiffUtil.ItemCallback<Exercise> diffCallback) {
        super(diffCallback);
    }

    protected ExerciseListAdapter(@NonNull AsyncDifferConfig<Exercise> config) {
        super(config);
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int _viewType) {
        return ExerciseViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise current = getItem(position);
        holder.bind(current.name());
    }

    public static class ExerciseDiff extends DiffUtil.ItemCallback<Exercise> {

        @Override
        public boolean areItemsTheSame(@NonNull Exercise oldItem, @NonNull Exercise newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Exercise oldItem, @NonNull Exercise newItem) {
            return oldItem.id() == newItem.id();
        }
    }
}
