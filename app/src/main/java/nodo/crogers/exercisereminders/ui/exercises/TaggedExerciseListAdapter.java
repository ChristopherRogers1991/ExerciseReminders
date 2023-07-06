package nodo.crogers.exercisereminders.ui.exercises;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import nodo.crogers.exercisereminders.database.Exercise;
import nodo.crogers.exercisereminders.database.Tag;

public class TaggedExerciseListAdapter extends BaseExpandableListAdapter {
    private final Map<Tag, List<Exercise>> taggedExercises;
    private final List<Tag> sortedTags;

    public TaggedExerciseListAdapter(Map<Tag, List<Exercise>> taggedExercises) {
        this.taggedExercises = taggedExercises;
        this.sortedTags = taggedExercises.keySet()
                .stream()
                .sorted(Comparator.comparing(Tag::name))
                .collect(Collectors.toList());
        this.sortedTags.sort(Comparator.comparing(Tag::name));
    }

    @Override
    public int getGroupCount() {
        return taggedExercises.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return taggedExercises.get(sortedTags.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return sortedTags.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return taggedExercises.get(sortedTags.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return sortedTags.get(groupPosition).id();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return ((Exercise) getChild(groupPosition, childPosition)).id();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TextView view = new TextView(parent.getContext());
        view.setText("> " + sortedTags.get(groupPosition).name());
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TextView view = new TextView(parent.getContext());
        view.setText(taggedExercises.get(sortedTags.get(groupPosition)).get(childPosition).name());
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
