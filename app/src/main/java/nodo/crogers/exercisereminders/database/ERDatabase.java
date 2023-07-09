package nodo.crogers.exercisereminders.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nodo.crogers.exercisereminders.R;
import nodo.crogers.exercisereminders.database.daos.ExerciseDao;
import nodo.crogers.exercisereminders.database.daos.ExerciseTagPairDao;
import nodo.crogers.exercisereminders.database.daos.TagDao;
import nodo.crogers.exercisereminders.database.entities.Exercise;
import nodo.crogers.exercisereminders.database.entities.ExerciseTagPair;
import nodo.crogers.exercisereminders.database.entities.Tag;
import nodo.crogers.exercisereminders.database.views.EnabledExercises;
import nodo.crogers.exercisereminders.database.views.ExercisesWithTags;

// TODO - export schema?
@Database(
        entities = {Exercise.class, Tag.class, ExerciseTagPair.class},
        views = {EnabledExercises.class, ExercisesWithTags.class},
        version = 1,
        exportSchema = false
)
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
                                executorService.execute(() -> initializeDb(context));
                            }
                        })
                        .build();
            }
        }
        return instance;
    }

    public void enableAsync(Exercise exercise) {
        ERDatabase.executorService.execute(() -> instance
                .exerciseDao()
                .enable(exercise));
    }

    public void disableAsync(Exercise exercise) {
        ERDatabase.executorService.execute(() -> instance
                .exerciseDao()
                .disable(exercise));
    }

    public void enableAsync(Tag tag) {
        ERDatabase.executorService.execute(() -> instance
                .tagDao()
                .enable(tag));
    }

    public void disableAsync(Tag tag) {
        ERDatabase.executorService.execute(() -> instance
                .tagDao()
                .disable(tag));
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
        Exercise chestStretch = new Exercise(context.getString(R.string.chest_stretch));

        Exercise jumpingJacks = new Exercise(context.getString(R.string.jumping_jacks));
        Exercise jogInPlace = new Exercise(context.getString(R.string.jog_in_place));

        db.tagExercises(upperBody, pushups, dips, pullUps, chinUps);
        db.tagExercises(lowerBody,
                        squats,
                        singleLegDeadlifts,
                        calfRaises,
                        lunges,
                        wallSit);
        db.tagExercises(abs, sitUps, crunches, legLifts, plank);
        db.tagExercises(stretch,
                        standingHamstringStretch,
                        calfStretch,
                        butterflyStretch,
                        chestStretch,
                        quadStretch);
        db.tagExercises(cardio, jumpingJacks, jogInPlace);
    }

    private void tagExercises(Tag tag, Exercise... exercises) {
        insertTags(tag);
        insertExercises(exercises);
        Tag tagWithCorrectId = tagDao().getByName(tag.name());
        insertAll(instance.exerciseTagPairDao()::insert,
                 Arrays.stream(exercises)
                         .map(exercise -> exerciseDao().getByName(exercise.name()))
                         .map(exerciseWithCorrectId ->
                                 new ExerciseTagPair(exerciseWithCorrectId, tagWithCorrectId)));
    }

    private void tagExercise(Exercise exercise, Tag... tags) {
        insertExercises(exercise);
        insertTags(tags);
        Exercise exerciseWithCorrectId = exerciseDao().getByName(exercise.name());
        insertAll(instance.exerciseTagPairDao()::insert,
                (ExerciseTagPair[]) Arrays.stream(tags)
                        .map(tag -> tagDao().getByName(tag.name()))
                        .map(tagWithCorrectId ->
                                new ExerciseTagPair(exerciseWithCorrectId, tagWithCorrectId))
                        .toArray());
    }

    public CompletableFuture<Void> tagExerciseAsync(Exercise exercise, Tag... tags) {
        return CompletableFuture.runAsync(() -> tagExercise(exercise, tags), executorService);
    }

    public CompletableFuture<Void> tagExercisesAsync(Tag tag, Exercise... exercises) {
        return CompletableFuture.runAsync(() -> tagExercises(tag, exercises), executorService);
    }

    private void insertExercises(Exercise... exercises) {
        insertAll(instance.exerciseDao()::insert, exercises);
    }

    private void insertTags(Tag... tags) {
        insertAll(instance.tagDao()::insert, tags);
    }

    @SafeVarargs
    private <T> void insertAll(Consumer<T> insertFunction, T... items) {
        for (T item : items) {
            insertFunction.accept(item);
        }
    }

    private <T> void insertAll(Consumer<T> insertFunction, Stream<T> items) {
        // Stream::forEach does not work for some unknown reason; must copy to a list.
        List<T> copy = items.collect(Collectors.toList());
        copy.forEach(insertFunction);
    }

}
