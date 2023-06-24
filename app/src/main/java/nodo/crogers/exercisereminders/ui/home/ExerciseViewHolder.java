package nodo.crogers.exercisereminders.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import nodo.crogers.exercisereminders.R;

public class ExerciseViewHolder extends RecyclerView.ViewHolder {
    private final TextView exerciseTextView;

    public ExerciseViewHolder(@NonNull View itemView) {
        super(itemView);
        exerciseTextView = itemView.findViewById(R.id.recyclerView_text);
    }

    public void bind(String text) {
        exerciseTextView.setText(text);
    }

    public static ExerciseViewHolder create(ViewGroup parent) {
        boolean attachToRoot = false;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, attachToRoot);
        return new ExerciseViewHolder(view);
    }
}
