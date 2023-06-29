package nodo.crogers.exercisereminders.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "exercise_to_tag",
        primaryKeys = {"exerciseId", "tagId"},
        foreignKeys = {
                @ForeignKey(
                        entity = Exercise.class,
                        parentColumns = {"id"},
                        childColumns = {"exerciseId"},
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(
                        entity = Tag.class,
                        parentColumns = {"id"},
                        childColumns = {"tagId"},
                        onDelete = ForeignKey.CASCADE)},
        indices = {
                @Index(
                        value = {"exerciseId", "tagId"},
                        unique = true)})
public class ExerciseTagPair {

    private int exerciseId;

    private int tagId;

    public ExerciseTagPair(int exerciseId, int tagId) {
        this.exerciseId = exerciseId;
        this.tagId = tagId;
    }

    public ExerciseTagPair(Exercise exercise, Tag tag) {
        this.exerciseId = exercise.id();
        this.tagId = tag.id();
    }

    public int exerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public int tagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
}
