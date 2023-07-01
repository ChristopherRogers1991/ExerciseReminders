package nodo.crogers.exercisereminders.ui.exercises;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import nodo.crogers.exercisereminders.R;
import nodo.crogers.exercisereminders.database.ERDatabase;
import nodo.crogers.exercisereminders.database.Exercise;
import nodo.crogers.exercisereminders.database.Tag;
import nodo.crogers.exercisereminders.database.TagDao;
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

        ExpandableListView expandableListView = root.findViewById(R.id.expandableList);
        ERDatabase db = ERDatabase.getInstance(requireContext());
        TagDao dao = db.tagDao();
        Map<Tag, List<Exercise>> tagsToExercises = new HashMap<>();
        CompletableFuture.runAsync(() -> {
            List<Tag> tags = dao.getAll();
            for (Tag tag : tags) {
                tagsToExercises.put(tag, dao.getExercises(tag));
            }
        }).thenRunAsync(() -> {
            expandableListView.post(() -> expandableListView.setAdapter(new TaggedExerciseListAdapter(tagsToExercises)));
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