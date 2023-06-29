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
import java.util.function.Consumer;
import java.util.stream.Stream;

import nodo.crogers.exercisereminders.R;

// TODO - export schema?
@Database(entities = {Exercise.class, Tag.class, ExerciseTagPair.class}, version = 1, exportSchema = false)
public abstract class ERDatabase extends RoomDatabase {
    private static ERDatabase instance;
    public static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public abstract ExerciseDao exerciseDao();

    public abstract TagDao tagDao();

    public abstract ExerciseTagPairDao exerciseTagPairDao();

    public static ERDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (ERDatabase.class) {
                instance = Room.databaseBuilder(context, ERDatabase.class, "er-database")
                        .addCallback(new Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);
                                executorService.execute(() -> {
                                    initializeDb(context);
                                });
                            }
                        })
                        .build();
            }
        }
        return instance;
    }

    public void enable(Exercise exercise) {
        ERDatabase.executorService.execute(() -> instance
                .exerciseDao()
                .enable(exercise));
    }

    public void disable(Exercise exercise) {
        ERDatabase.executorService.execute(() -> instance
                .exerciseDao()
                .disable(exercise));
    }

    public void enable(Tag tag) {
        ERDatabase.executorService.execute(() -> instance
                .tagDao()
                .enable(tag));
    }

    public void disable(Tag tag) {
        ERDatabase.executorService.execute(() -> instance
                .tagDao()
                .disable(tag));
    }

    private static void initializeDb(Context context) {
        ERDatabase db = getInstance(context);

        int tagId = 1;
        Tag upperBody = new Tag("Upper Body", tagId++);
        Tag lowerBody = new Tag("Lower Body", tagId++);
        Tag abs = new Tag("Abs", tagId++);
        Tag stretch = new Tag("Stretch", tagId++);
        Tag cardio = new Tag("Cardio", tagId++);

        int exerciseId = 1;
        Exercise pushups = new Exercise(context.getString(R.string.push_ups), exerciseId++);
        Exercise dips = new Exercise(context.getString(R.string.dips), exerciseId++);
        Exercise pullUps = new Exercise(context.getString(R.string.pull_ups), exerciseId++);
        Exercise chinUps = new Exercise(context.getString(R.string.chin_ups), exerciseId++);

        Exercise squats = new Exercise(context.getString(R.string.squats), exerciseId++);
        Exercise singleLegDeadlifts =
                new Exercise(context.getString(R.string.single_leg_deadlifts), exerciseId++);
        Exercise calfRaises = new Exercise(context.getString(R.string.calf_raises), exerciseId++);
        Exercise lunges = new Exercise(context.getString(R.string.lunges), exerciseId++);
        Exercise wallSit = new Exercise(context.getString(R.string.wall_sit), exerciseId++);

        Exercise sitUps = new Exercise(context.getString(R.string.sit_ups), exerciseId++);
        Exercise legLifts = new Exercise(context.getString(R.string.leg_lifts), exerciseId++);
        Exercise plank = new Exercise(context.getString(R.string.plank), exerciseId++);
        Exercise crunches = new Exercise(context.getString(R.string.crunches), exerciseId++);

        Exercise standingHamstringStretch =
                new Exercise(context.getString(R.string.standing_hamstring_stretch), exerciseId++);
        Exercise calfStretch = new Exercise(context.getString(R.string.calf_stretch), exerciseId++);
        Exercise butterflyStretch = new Exercise(context.getString(R.string.butterfly_stretch), exerciseId++);
        Exercise quadStretch = new Exercise(context.getString(R.string.quad_stretch), exerciseId++);

        Exercise jumpingJacks = new Exercise(context.getString(R.string.jumping_jacks), exerciseId++);

        db.tagExercises(upperBody, pushups, dips, pullUps, chinUps);
        db.tagExercises(lowerBody,
                        squats,
                        singleLegDeadlifts,
                        calfRaises,
                        lunges,
                        wallSit,
                        standingHamstringStretch,
                        quadStretch,
                        calfStretch);
        db.tagExercises(abs, sitUps, crunches, legLifts, plank);
        db.tagExercises(stretch,
                        standingHamstringStretch,
                        calfStretch,
                        butterflyStretch,
                        quadStretch);
        db.tagExercises(cardio, jumpingJacks);
    }

    private void tagExercises(Tag tag, Exercise... exercises) {
        insertTags(tag);
        insertExercises(exercises);
        insertAll(instance.exerciseTagPairDao()::insert,
                 Arrays.stream(exercises)
                        .map(exercise -> new ExerciseTagPair(exercise, tag)));
    }

    private void tagExercise(Exercise exercise, Tag... tags) {
        insertExercises(exercise);
        insertTags(tags);
        insertAll(instance.exerciseTagPairDao()::insert,
                (ExerciseTagPair[]) Arrays.stream(tags)
                        .map(tag -> new ExerciseTagPair(exercise, tag))
                        .toArray());
    }

    private void insertExercises(Exercise... exercises) {
        insertAll(instance.exerciseDao()::insert, exercises);
    }

    private void insertTags(Tag... tags) {
        insertAll(instance.tagDao()::insert, tags);
    }

    @SafeVarargs
    private final <T> void insertAll(Consumer<T> insertFunction, T... items) {
        for (T item : items) {
            insertFunction.accept(item);
        }
    }

    private final <T> void insertAll(Consumer<T> insertFunction, Stream<T> items) {
        items.forEach(insertFunction);
    }


}
