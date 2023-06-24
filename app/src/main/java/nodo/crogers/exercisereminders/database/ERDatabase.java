package nodo.crogers.exercisereminders.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// TODO - export schema?
@Database(entities = {Exercise.class}, version = 1, exportSchema = false)
public abstract class ERDatabase extends RoomDatabase {
    private static ERDatabase instance;
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public abstract ExerciseDao exerciseDao();

    public static ERDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (ERDatabase.class) {
                instance = Room.databaseBuilder(context, ERDatabase.class, "er-database")
                        .addCallback(new Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);
                                executorService.execute(() -> {
                                    ExerciseDao dao = instance.exerciseDao();
                                    for (Exercise exercise : Exercise.getDefaults()) {
                                        dao.insert(exercise);
                                    }
                                });
                            }
                        })
                        .build();
            }
        }
        return instance;
    }
}
