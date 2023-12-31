package nodo.crogers.exercisereminders.ui.exercises;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import nodo.crogers.exercisereminders.R;
import nodo.crogers.exercisereminders.database.ERDatabase;
import nodo.crogers.exercisereminders.database.entities.Exercise;
import nodo.crogers.exercisereminders.database.entities.Tag;
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

        exercisesViewModel.getTagsToExercises()
                .observe(getViewLifecycleOwner(), this::onTagsToExercisesUpdate);

        return root;
    }

    private void onTagsToExercisesUpdate(Map<Tag, List<Exercise>> map) {
        ExpandableListView view = binding.getRoot().findViewById(R.id.expandableList);
        view = view != null ? view : requireActivity().findViewById(R.id.expandableList);
        TaggedExerciseListAdapter adapter =
                (TaggedExerciseListAdapter) view.getExpandableListAdapter();
        Map<String, List<String>> newStrings = toStrings(map);
        Map<String, List<String>> currentStrings =
                adapter == null ? Map.of() : toStrings(adapter.getTaggedExercises());
        if (!newStrings.equals(currentStrings)) {
            view.setAdapter(new TaggedExerciseListAdapter(getViewLifecycleOwner(), map));
        }
    }

    private Map<String, List<String>> toStrings(Map<Tag, List<Exercise>> tagListMap) {
        Map<String, List<String>> result = new HashMap<>();
        tagListMap.forEach((tag, exercises) -> result.put(tag.name(),
                exercises.stream().map(Exercise::name).collect(Collectors.toList())));
        return result;
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
            final EditText exerciseNameInput = Objects.requireNonNull(createExerciseDialog.findViewById(R.id.exerciseNameText));
            final EditText tagNameInput = Objects.requireNonNull(createExerciseDialog.findViewById(R.id.tagNameText));
            String exerciseName = exerciseNameInput.getText().toString();
            if (exerciseName.equals("")) {
                dialog.dismiss();
            }
            else  {
                ERDatabase.getInstance(context).tagExercisesAsync(
                                new Tag(tagNameInput.getText().toString()),
                                new Exercise(exerciseNameInput.getText().toString()));
            }
        });

        createExerciseDialog.show();
    }
}