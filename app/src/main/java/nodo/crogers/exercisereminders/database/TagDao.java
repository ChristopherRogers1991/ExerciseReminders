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

    @Query("SELECT * FROM tag ORDER BY name ASC")
    LiveData<List<Tag>> getAll();

    @Query("SELECT * FROM tag where enabled = 1 AND count = (SELECT min(count) from tag where enabled = 1)")
    List<Exercise> getEligible();

    @Query("SELECT * from Exercise where Exercise.id in (SELECT exerciseId from exercise_to_tag where tagId = :tagId)")
    LiveData<List<Exercise>> getExercises(int tagId);

    default LiveData<List<Exercise>> getExercises(Tag tag) {
        return getExercises(tag.id());
    }

    @Update
    void update(Tag tag);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Tag tag);

    @Delete
    void delete(Tag tag);

    default void enable(Tag tag) {
        int maxCount = getEligible().stream().mapToInt(Exercise::count).max().orElse(0);
        tag.setCount(maxCount);
        tag.setEnabled(1);
        update(tag);
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
