package nodo.crogers.exercisereminders.ui.exercises;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import nodo.crogers.exercisereminders.database.Tag;

public class TagListAdapter extends ListAdapter<Tag, TagViewHolder> {
    private final ExercisesViewModel exercisesViewModel;
    protected TagListAdapter(@NonNull DiffUtil.ItemCallback<Tag> diffCallback, ExercisesViewModel exercisesViewModel) {
        super(diffCallback);
        this.exercisesViewModel = exercisesViewModel;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int _viewType) {
        return TagViewHolder.create(parent, exercisesViewModel);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Tag current = getItem(position);
        holder.bind(current);
    }

    public static class TagDiff extends DiffUtil.ItemCallback<Tag> {

        @Override
        public boolean areItemsTheSame(@NonNull Tag oldItem, @NonNull Tag newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Tag oldItem, @NonNull Tag newItem) {
            return oldItem.id() == newItem.id();
        }
    }
}
