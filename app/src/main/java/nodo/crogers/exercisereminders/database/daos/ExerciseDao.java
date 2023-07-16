package nodo.crogers.exercisereminders.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import nodo.crogers.exercisereminders.database.entities.Exercise;
import nodo.crogers.exercisereminders.database.entities.Tag;

@Dao
public interface ExerciseDao {

    String getTagsQuery = """
               SELECT
                   *
               FROM
                   tag
               WHERE
                   tag.id IN (SELECT tagId FROM exercise_to_tag WHERE exerciseId = :exerciseId)
               ORDER BY tag.name ASC
            """;
    @Query("SELECT * FROM Exercise WHERE name = :name")
    Exercise getByName(String name);

    @Query("SELECT * FROM Exercise ORDER BY name ASC")
    LiveData<List<Exercise>> getAll();

    @Query("""
               SELECT
                   *
               FROM
                   enabled_exercises
               WHERE
                   count = (SELECT min(count) FROM enabled_exercises WHERE enabled = 1)
           """)
    List<Exercise> getEligible();

    @Query(getTagsQuery)
    LiveData<List<Tag>> getTagsLive(int exerciseId);

    @Query(getTagsQuery)
    List<Tag> getTags(int exerciseId);

    default LiveData<List<Tag>> getTagsLive(Exercise exercise) {
        return getTagsLive(exercise.id());
    }

    default List<Tag> getTags(Exercise exercise) {
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
