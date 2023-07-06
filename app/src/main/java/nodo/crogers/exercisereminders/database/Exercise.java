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

    @Ignore
    public Exercise(String name, int id) {
        this.name = name;
        this.id = id;
        this.enabled = 1;
    }

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

}
