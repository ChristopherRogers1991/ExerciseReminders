package nodo.crogers.exercisereminders.ui.home;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import nodo.crogers.exercisereminders.database.ERDatabase;
import nodo.crogers.exercisereminders.database.Exercise;
import nodo.crogers.exercisereminders.database.ExerciseDao;

public class ExercisesViewModel extends AndroidViewModel {

    private ExerciseDao exerciseDao;
    private LiveData<List<Exercise>> allExercises;

    public ExercisesViewModel(Application application) {
        super(application);
        exerciseDao = ERDatabase.getInstance(application).exerciseDao();
        allExercises = exerciseDao.getAll();
    }

    public LiveData<List<Exercise>> getAllExercises() {
        return allExercises;
    }

    public static final String[] EXERCISES = {
            "Push-ups",
            "Sit-ups",
            "Leg Lifts",
            "Crunches",
            "Plank",
            "Squats",
            "Single-leg dead-lifts",
            "Standing Hamstring Stretch",
            "Calf Stretch",
            "Butterfly Stretch",
            "Dips",
            "Pull-ups",
            "Chin-ups"
    };

}