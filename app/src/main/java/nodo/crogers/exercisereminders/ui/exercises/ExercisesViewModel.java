package nodo.crogers.exercisereminders.ui.exercises;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nodo.crogers.exercisereminders.database.ERDatabase;
import nodo.crogers.exercisereminders.database.Exercise;
import nodo.crogers.exercisereminders.database.Tag;

public class ExercisesViewModel extends AndroidViewModel {

    private final MutableLiveData<Map<Tag, List<Exercise>>> tagsToExercises;
    private final ERDatabase db;

    public ExercisesViewModel(Application application) {
        super(application);
        db = ERDatabase.getInstance(application);
        tagsToExercises = new MutableLiveData<>();
        LiveData<List<Tag>> allTags = db.tagDao().getAllLive();
        allTags.observeForever(tags -> {
            Map<Tag, List<Exercise>> map = new HashMap<>();
            ERDatabase.executorService.execute(() -> {
                for (Tag tag : tags) {
                    map.put(tag, db.tagDao().getExercises(tag));
                }
                tagsToExercises.postValue(map);
            });
        });
        LiveData<List<Exercise>> allExercises = db.exerciseDao().getAll();
        allExercises.observeForever(_exercises -> {
            Map<Tag, List<Exercise>> map = new HashMap<>();
            ERDatabase.executorService.execute(() -> {
                for (Tag tag : db.tagDao().getAll()) {
                    map.put(tag, db.tagDao().getExercises(tag));
                }
                tagsToExercises.postValue(map);
            });
        });
    }

    public LiveData<Map<Tag, List<Exercise>>> getTagsToExercises() {
        return tagsToExercises;
    }
}