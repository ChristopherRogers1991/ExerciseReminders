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
    @Query("SELECT * FROM exercise")
    LiveData<List<Exercise>> getAll();

    @Query("SELECT * FROM exercise where enabled = 1 AND count = (SELECT min(count) from exercise)")
    List<Exercise> getEligible();

    @Update
    void update(Exercise exercise);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Exercise exercise);

    @Delete
    void delete(Exercise exercise);
}
