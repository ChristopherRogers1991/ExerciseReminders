package nodo.crogers.exercisereminders.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;

import nodo.crogers.exercisereminders.database.entities.ExerciseTagPair;

@Dao
public interface ExerciseTagPairDao {
    @Insert
    void insert(ExerciseTagPair pair);

}
