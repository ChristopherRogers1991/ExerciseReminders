package nodo.crogers.exercisereminders.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import nodo.crogers.exercisereminders.ExerciseAlarm;
import nodo.crogers.exercisereminders.R;
import nodo.crogers.exercisereminders.databinding.FragmentHomeBinding;

public class ExercisesFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button button = root.findViewById(R.id.button);
        button.setOnClickListener(this::buttonClicked);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        final ExerciseListAdapter adapter = new ExerciseListAdapter(new ExerciseListAdapter.ExerciseDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ExercisesViewModel exercisesViewModel =
                new ViewModelProvider(this).get(ExercisesViewModel.class);

        exercisesViewModel.getAllExercises().observe(getViewLifecycleOwner(), adapter::submitList);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void buttonClicked(View view) {
        ExerciseAlarm.showNotification(view.getContext());
    }
}