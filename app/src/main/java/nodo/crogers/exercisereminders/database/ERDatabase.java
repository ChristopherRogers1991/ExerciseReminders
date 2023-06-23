package nodo.crogers.exercisereminders.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

// TODO - export schema?
@Database(entities = {Exercise.class}, version = 1, exportSchema = false)
public abstract class ERDatabase extends RoomDatabase {
    private static ERDatabase instance;

    public abstract ExerciseDao exerciseDao();

    public static ERDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, ERDatabase.class, "er-database")
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);

                        }
                    })
                    .build();
        }
        return instance;
    }
}
