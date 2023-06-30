package nodo.crogers.exercisereminders.ui.exercises;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import nodo.crogers.exercisereminders.R;
import nodo.crogers.exercisereminders.database.ERDatabase;
import nodo.crogers.exercisereminders.database.Exercise;
import nodo.crogers.exercisereminders.database.Tag;
import nodo.crogers.exercisereminders.databinding.FragmentExercisesBinding;

public class ExercisesFragment extends Fragment {

    private FragmentExercisesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentExercisesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button button = root.findViewById(R.id.button);
        button.setOnClickListener(this::buttonClicked);

        ExercisesViewModel exercisesViewModel =
                new ViewModelProvider(this).get(ExercisesViewModel.class);

        RecyclerView tagRecyclerView = root.findViewById(R.id.tagRecyclerView);
        final TagListAdapter tagListAdapter = new TagListAdapter(new TagListAdapter.TagDiff(), exercisesViewModel);
        tagRecyclerView.setAdapter(tagListAdapter);
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView exerciseRecyclerView = root.findViewById(R.id.exerciseRecyclerView);
        final ExerciseListAdapter exerciseListAdapter = new ExerciseListAdapter(new ExerciseListAdapter.ExerciseDiff());
        exerciseRecyclerView.setAdapter(exerciseListAdapter);
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        exercisesViewModel.getAllTags().observe(getViewLifecycleOwner(), newTags -> {
            List<String> currentTagNames = tagListAdapter.getCurrentList()
                    .stream()
                    .map(Tag::name)
                    .collect(Collectors.toList());
            List<String> newExerciseNames = newTags.stream()
                    .map(Tag::name)
                    .collect(Collectors.toList());
            if (!newExerciseNames.equals(currentTagNames)) {
                tagListAdapter.submitList(newTags);
            }
        });

        exercisesViewModel.getTaggedExercises().observe(getViewLifecycleOwner(), newExercises -> {
            List<String> currentExerciseNames = exerciseListAdapter.getCurrentList()
                    .stream()
                    .map(Exercise::name)
                    .collect(Collectors.toList());
            List<String> newExerciseNames = newExercises.stream()
                    .map(Exercise::name)
                    .collect(Collectors.toList());
            if (!newExerciseNames.equals(currentExerciseNames)) {
                exerciseListAdapter.submitList(newExercises);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void buttonClicked(View view) {
        Context context = this.requireContext();
        AlertDialog createExerciseDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.create_new_exercise)
                .setView(R.layout.create_exercise_dialog)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, ((dialog, which) -> dialog.cancel()))
                .create();

        createExerciseDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok), (dialog, which) -> {
            final EditText input = Objects.requireNonNull(createExerciseDialog.findViewById(R.id.exerciseNameText));
            String exerciseName = input.getText().toString();
            if (!exerciseName.equals("")) {
                ERDatabase.executorService.execute(() ->
                        ERDatabase.getInstance(context)
                                .exerciseDao().
                                insert(new Exercise(input.getText().toString())));
            }
            dialog.dismiss();
        });

        createExerciseDialog.show();
    }
}