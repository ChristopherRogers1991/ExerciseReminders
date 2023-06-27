package nodo.crogers.exercisereminders.database;

import android.content.Context;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import nodo.crogers.exercisereminders.R;

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

    @Ignore
    public Exercise(String name) {
        this.name = name;
        this.enabled = 1;
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

    public static Exercise[] getDefaults(Context context) {
        return new Exercise[]{
                new Exercise(context.getString(R.string.push_ups)),
                new Exercise(context.getString(R.string.sit_ups)),
                new Exercise(context.getString(R.string.leg_lifts)),
                new Exercise(context.getString(R.string.crunches)),
                new Exercise(context.getString(R.string.plank)),
                new Exercise(context.getString(R.string.squats)),
                new Exercise(context.getString(R.string.single_leg_deadlifts)),
                new Exercise(context.getString(R.string.standing_hamstring_stretch)),
                new Exercise(context.getString(R.string.calf_stretch)),
                new Exercise(context.getString(R.string.butterfly_stretch)),
                new Exercise(context.getString(R.string.dips)),
                new Exercise(context.getString(R.string.jumping_jacks)),
                new Exercise(context.getString(R.string.quad_stretch)),
                new Exercise(context.getString(R.string.calf_raises)),
                new Exercise(context.getString(R.string.lunges)),
                new Exercise(context.getString(R.string.wall_sit)),
                new Exercise(context.getString(R.string.pull_ups)),
                new Exercise(context.getString(R.string.chin_ups))
        };
    }
}
