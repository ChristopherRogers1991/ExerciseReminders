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

import nodo.crogers.exercisereminders.R;

// TODO - export schema?
@Database(entities = {Exercise.class, Tag.class, ExerciseTagPair.class}, version = 1, exportSchema = false)
public abstract class ERDatabase extends RoomDatabase {
    private static ERDatabase instance;
    public static final ExecutorService executorService = Executors.newSingleThreadExecutor();

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
                                    ExerciseDao dao = instance.exerciseDao();
                                    for (Exercise exercise : Exercise.getDefaults(context)) {
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

    public void insertExercises(Exercise... exercises) {
        insertAll(instance.exerciseDao()::insert, exercises);
    }

    public void insertTags(Tag... tags) {
        insertAll(instance.tagDao()::insert, tags);
    }

    public void tagExercise(Exercise exercise, Tag... tags) {
        insertExercises(exercise);
        insertTags(tags);
        insertAll(instance.exerciseTagPairDao()::insert,
                (ExerciseTagPair[]) Arrays.stream(tags)
                        .map(tag -> new ExerciseTagPair(exercise, tag))
                        .toArray());
    }

    public void tagExercises(Tag tag, Exercise... exercises) {
        insertTags(tag);
        insertExercises(exercises);
        insertAll(instance.exerciseTagPairDao()::insert,
                (ExerciseTagPair[]) Arrays.stream(exercises)
                        .map(exercise -> new ExerciseTagPair(exercise, tag))
                        .toArray());
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

    @SafeVarargs
    private final <T> void insertAll(Consumer<T> insertFunction, T... items) {
        executorService.execute(() -> {
            for (T item : items) {
                insertFunction.accept(item);
            }
        });
    }

    private static void initializeDb(Context context) {
        ERDatabase db = getInstance(context);

        Tag upperBody = new Tag("Upper Body");
        Tag lowerBody = new Tag("Lower Body");
        Tag abs = new Tag("Abs");
        Tag stretch = new Tag("Stretch");
        Tag cardio = new Tag("Cardio");

        Exercise pushups = new Exercise(context.getString(R.string.push_ups));
        Exercise dips = new Exercise(context.getString(R.string.dips));
        Exercise pullUps = new Exercise(context.getString(R.string.pull_ups));
        Exercise chinUps = new Exercise(context.getString(R.string.chin_ups));

        Exercise squats = new Exercise(context.getString(R.string.squats));
        Exercise singleLegDeadlifts =
                new Exercise(context.getString(R.string.single_leg_deadlifts));
        Exercise calfRaises = new Exercise(context.getString(R.string.calf_raises));
        Exercise lunges = new Exercise(context.getString(R.string.lunges));
        Exercise wallSit = new Exercise(context.getString(R.string.wall_sit));

        Exercise sitUps = new Exercise(context.getString(R.string.sit_ups));
        Exercise legLifts = new Exercise(context.getString(R.string.leg_lifts));
        Exercise plank = new Exercise(context.getString(R.string.plank));
        Exercise crunches = new Exercise(context.getString(R.string.crunches));

        Exercise standingHamstringStretch =
                new Exercise(context.getString(R.string.standing_hamstring_stretch));
        Exercise calfStretch = new Exercise(context.getString(R.string.calf_stretch));
        Exercise butterflyStretch = new Exercise(context.getString(R.string.butterfly_stretch));
        Exercise quadStretch = new Exercise(context.getString(R.string.quad_stretch));

        Exercise jumpingJacks = new Exercise(context.getString(R.string.jumping_jacks));

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
}
