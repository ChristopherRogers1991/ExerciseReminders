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
public interface TagDao {

    @Query("SELECT * FROM Tag WHERE name = :name")
    Tag getByName(String name);

    @Query("SELECT * FROM tag ORDER BY name ASC")
    LiveData<List<Tag>> getAllLive();

    @Query("SELECT * FROM tag ORDER BY name ASC")
    List<Tag> getAll();

    @Query("SELECT * FROM tag where enabled = 1 AND count = (SELECT min(count) from tag where enabled = 1)")
    List<Exercise> getEligible();

    @Query("SELECT * from Exercise where Exercise.id in (SELECT exerciseId from exercise_to_tag where tagId = :tagId)")
    List<Exercise> getExercises(int tagId);

    default List<Exercise> getExercises(Tag tag) {
        return getExercises(tag.id());
    }

    @Update
    void update(Tag tag);

    @Query("""
           UPDATE
               exercise
           SET
               count = (SELECT min(count) from enabled_exercises)
           WHERE id IN (SELECT exercise.id FROM exercises_with_tags WHERE tagId = :tagId)
    """)
    void updateExerciseCounts(int tagId);

    default void updateExerciseCounts(Tag tag) {
        updateExerciseCounts(tag.id());
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Tag tag);

    @Delete
    void delete(Tag tag);

    default void enable(Tag tag) {
        int maxCount = getEligible().stream().mapToInt(Exercise::count).max().orElse(0);
        tag.setCount(maxCount);
        tag.setEnabled(1);
        update(tag);
        updateExerciseCounts(tag);
    }

    default void disable(Tag tag) {
        tag.setEnabled(0);
        update(tag);
    }

    default void incrementCount(Tag tag) {
        tag.setCount(tag.count() + 1);
        update(tag);
    }
}
