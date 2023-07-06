package nodo.crogers.exercisereminders.database;

import androidx.room.DatabaseView;

@DatabaseView(value = """
SELECT
    exercise.*,
    tag.id as tagId
FROM
    exercise
    JOIN exercise_to_tag ON exercise.id = exercise_to_tag.exerciseId
    JOIN tag on exercise_to_tag.tagId = tag.id
""",
viewName = "exercises_with_tags")
public class ExercisesWithTags {
}
