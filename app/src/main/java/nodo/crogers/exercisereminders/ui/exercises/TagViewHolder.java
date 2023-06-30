package nodo.crogers.exercisereminders.ui.exercises;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import nodo.crogers.exercisereminders.R;
import nodo.crogers.exercisereminders.database.ERDatabase;
import nodo.crogers.exercisereminders.database.Tag;

public class TagViewHolder extends RecyclerView.ViewHolder {
    private final TextView tagTextView;
    private final CheckBox enabledCheckBox;
    private final View itemView;
    private final ExercisesViewModel exercisesViewModel;

    public TagViewHolder(@NonNull View itemView, ExercisesViewModel exercisesViewModel) {
        super(itemView);
        this.itemView = itemView;
        this.exercisesViewModel = exercisesViewModel;
        tagTextView = itemView.findViewById(R.id.recyclerView_text);
        enabledCheckBox = itemView.findViewById(R.id.recyclerView_checkBox);
    }

    public void bind(Tag tag) {
        enabledCheckBox.setOnCheckedChangeListener(null);
        tagTextView.setOnClickListener(null);

        tagTextView.setText(tag.name());
        tagTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exercisesViewModel.setTag(tag);
//                tagTextView.getRootView().findViewById(R.id.tagRecyclerView).setVisibility(View.GONE);
//                tagTextView.getRootView().findViewById(R.id.exerciseRecyclerView).setVisibility(View.VISIBLE);
                View tags = tagTextView.getRootView().findViewById(R.id.tagRecyclerView);
                View exercises = tagTextView.getRootView().findViewById(R.id.exerciseRecyclerView);
                ObjectAnimator tagAnimator = ObjectAnimator.ofFloat(tags, "translationX", -1F * tags.getWidth());
                exercises.setX(tags.getWidth());
                exercises.setVisibility(View.VISIBLE);
                exercises.animate()
                        .x(0F)
                        .setDuration(500)
                        .setListener(null);
                tagAnimator.setDuration(500);
                tagAnimator.start();
            }
        });
        enabledCheckBox.setChecked(tag.enabled() == 1);
        enabledCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ERDatabase.executorService.execute(() -> ERDatabase.getInstance(itemView.getContext())
                        .tagDao()
                        .enable(tag));
            } else {
                ERDatabase.executorService.execute(() -> ERDatabase.getInstance(itemView.getContext())
                        .tagDao()
                        .disable(tag));
            }
        });
    }

    public static TagViewHolder create(ViewGroup parent, ExercisesViewModel exercisesViewModel) {
        boolean attachToRoot = false;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, attachToRoot);
        return new TagViewHolder(view, exercisesViewModel);
    }
}
