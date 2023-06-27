package nodo.crogers.exercisereminders.ui.exercises;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import nodo.crogers.exercisereminders.database.ERDatabase;
import nodo.crogers.exercisereminders.database.Exercise;
import nodo.crogers.exercisereminders.database.ExerciseDao;

public class ExercisesViewModel extends AndroidViewModel {

    private final LiveData<List<Exercise>> allExercises;

    public ExercisesViewModel(Application application) {
        super(application);
        ExerciseDao exerciseDao = ERDatabase.getInstance(application).exerciseDao();
        allExercises = exerciseDao.getAll();
    }

    public LiveData<List<Exercise>> getAllExercises() {
        return allExercises;
    }

}