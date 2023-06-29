package nodo.crogers.exercisereminders.ui.exercises;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import nodo.crogers.exercisereminders.database.ERDatabase;
import nodo.crogers.exercisereminders.database.Exercise;
import nodo.crogers.exercisereminders.database.Tag;

public class ExercisesViewModel extends AndroidViewModel {

    private MutableLiveData<List<Exercise>> taggedExercises;
    private final LiveData<List<Tag>> allTags;
    private final ERDatabase db;

    public ExercisesViewModel(Application application) {
        super(application);
        db = ERDatabase.getInstance(application);
        taggedExercises = new MutableLiveData<>();
        allTags = db.tagDao().getAll();
    }

    public LiveData<List<Exercise>> getTaggedExercises() {
        return taggedExercises;
    }

    public void setTag(Tag tag) {
        ERDatabase.executorService.execute(() -> {
                List<Exercise> exercises = db.tagDao().getExercises(tag);
                taggedExercises.postValue(exercises);
        });
    }

    public LiveData<List<Tag>> getAllTags() {
        return allTags;
    }

}