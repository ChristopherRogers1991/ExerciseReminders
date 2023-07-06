package nodo.crogers.exercisereminders.database;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface ExerciseTagPairDao {
    @Insert
    void insert(ExerciseTagPair pair);

}
