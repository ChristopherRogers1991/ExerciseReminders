package nodo.crogers.exercisereminders.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import nodo.crogers.exercisereminders.ExerciseAlarmReceiver;
import nodo.crogers.exercisereminders.R;
import nodo.crogers.exercisereminders.databinding.FragmentHomeBinding;

public class ExercisesFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        ExercisesViewModel exercisesViewModel =
                new ViewModelProvider(this).get(ExercisesViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button button = (Button) root.findViewById(R.id.button);
        button.setOnClickListener(this::buttonClicked);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void buttonClicked(View view) {
        ExerciseAlarmReceiver.showNotification(view.getContext());
    }
}