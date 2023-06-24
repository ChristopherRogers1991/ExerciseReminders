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

    @ColumnInfo(defaultValue = "1")
    private int enabled;

    @ColumnInfo(defaultValue = "0")
    private int count;

    public Exercise(String name, int enabled) {
        this.name = name;
        this.enabled = enabled >= 1 ? 1 : 0;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public int enabled() {
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

    public void setEnabled(int enabled) {
        this.enabled = enabled >= 1 ? 1 : 0;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static Exercise[] getDefaults() {
        int enabled = 1;
        return new Exercise[]{
                new Exercise("Push-ups", enabled),
                new Exercise("Sit-ups", enabled),
                new Exercise("Leg Lifts", enabled),
                new Exercise("Crunches", enabled),
                new Exercise("Plank", enabled),
                new Exercise("Squats", enabled),
                new Exercise("Single-leg dead-lifts", enabled),
                new Exercise("Standing Hamstring Stretch", enabled),
                new Exercise("Calf Stretch", enabled),
                new Exercise("Butterfly Stretch", enabled),
                new Exercise("Dips", enabled),
                new Exercise("Pull-ups", enabled),
                new Exercise("Chin-ups", enabled)
        };
    }
}
