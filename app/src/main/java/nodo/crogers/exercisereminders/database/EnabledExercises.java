package nodo.crogers.exercisereminders.database;

import androidx.room.DatabaseView;

@DatabaseView(value = """
                   SELECT
                       exercise.*
                   FROM
                       exercise
                           JOIN exercise_to_tag ON exercise.id = exercise_to_tag.exerciseId
                           JOIN tag on exercise_to_tag.tagId = tag.id
                   WHERE
                       exercise.enabled = 1
                       AND tag.enabled = 1
""", viewName = "enabled_exercises")
public class EnabledExercises {
}
