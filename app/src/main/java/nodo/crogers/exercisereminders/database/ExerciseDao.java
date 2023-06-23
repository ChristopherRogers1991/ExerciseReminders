package nodo.crogers.exercisereminders.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExerciseDao {
    @Query("SELECT * FROM exercise")
    List<Exercise> getAll();

    @Query("SELECT * FROM exercise WHERE category = (:category)")
    List<Exercise> loadAllByIds(String category);

    @Insert
    void insert(Exercise exercise);

    @Delete
    void delete(Exercise user);
}
