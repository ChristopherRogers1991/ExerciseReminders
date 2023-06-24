package nodo.crogers.exercisereminders.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value="name", unique = true)})
public class Exercise {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String name;

    @ColumnInfo(defaultValue = "true")
    private boolean enabled;

    @ColumnInfo(defaultValue = "0")
    private int count;

    public Exercise(String name) {
        this.name = name;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public boolean enabled() {
        return enabled;
    }

    public int count() {
        return count;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static Exercise[] getDefaults() {
        return new Exercise[]{
                new Exercise("Push-ups"),
                new Exercise("Sit-ups"),
                new Exercise("Leg Lifts"),
                new Exercise("Crunches"),
                new Exercise("Plank"),
                new Exercise("Squats"),
                new Exercise("Single-leg dead-lifts"),
                new Exercise("Standing Hamstring Stretch"),
                new Exercise("Calf Stretch"),
                new Exercise("Butterfly Stretch"),
                new Exercise("Dips"),
                new Exercise("Pull-ups"),
                new Exercise("Chin-ups")
        };
    }
}
