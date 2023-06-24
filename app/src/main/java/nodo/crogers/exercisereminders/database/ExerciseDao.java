package nodo.crogers.exercisereminders.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExerciseDao {
    @Query("SELECT * FROM exercise")
    LiveData<List<Exercise>> getAll();

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Exercise exercise);

    @Delete
    void delete(Exercise exercise);
}
