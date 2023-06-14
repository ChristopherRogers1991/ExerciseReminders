package nodo.crogers.exercisereminders.ui.gallery;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import nodo.crogers.exercisereminders.ExerciseAlarm;
import nodo.crogers.exercisereminders.PreferenceManager;
import nodo.crogers.exercisereminders.R;
import nodo.crogers.exercisereminders.databinding.FragmentGalleryBinding;

public class SettingsFragment extends Fragment {

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Context context = this.getContext();
        PreferenceManager preferenceManager = PreferenceManager.getInstance(context);
        Button pauseButton = root.findViewById(R.id.pauseButton);
        Runnable setButtonText = () -> pauseButton.setText(preferenceManager.isPaused() ? "Resume" : "Pause");
        setButtonText.run();
        pauseButton.setOnClickListener(_view -> {
            preferenceManager.togglePaused();
            setButtonText.run();
            if (!preferenceManager.isPaused()) {
                ExerciseAlarm.scheduleNext(Objects.requireNonNull(context));
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}