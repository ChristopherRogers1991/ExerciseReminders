package nodo.crogers.exercisereminders.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExerciseDao {
    @Query("SELECT * FROM Exercise WHERE name = :name")
    Exercise getByName(String name);

    @Query("SELECT * FROM Exercise ORDER BY name ASC")
    LiveData<List<Exercise>> getAll();

    @Query("SELECT * FROM Exercise where enabled = 1 AND count = (SELECT min(count) from Exercise where enabled = 1)")
    List<Exercise> getEligible();

    @Query("SELECT * FROM tag WHERE tag.id IN (SELECT tagId FROM exercise_to_tag WHERE exerciseId = :exerciseId)")
    LiveData<List<Tag>> getTags(int exerciseId);

    default LiveData<List<Tag>> getTags(Exercise exercise) {
        return getTags(exercise.id());
    }
    @Update
    void update(Exercise exercise);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Exercise exercise);

    @Delete
    void delete(Exercise exercise);

    default void enable(Exercise exercise) {
        int maxCount = getEligible().stream().mapToInt(Exercise::count).max().orElse(0);
        exercise.setCount(maxCount);
        exercise.setEnabled(1);
        update(exercise);
    }

    default void disable(Exercise exercise) {
        exercise.setEnabled(0);
        update(exercise);
    }

    default void incrementCount(Exercise exercise) {
        exercise.setCount(exercise.count() + 1);
        update(exercise);
    }
}
