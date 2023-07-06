package nodo.crogers.exercisereminders.ui.exercises;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import nodo.crogers.exercisereminders.R;
import nodo.crogers.exercisereminders.database.ERDatabase;
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

    public Map<Tag, List<Exercise>> getTaggedExercises() {
        return taggedExercises;
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
        Context context = parent.getContext();
        LayoutInflater inflater = context.getSystemService(LayoutInflater.class);
        View groupView = inflater.inflate(R.layout.expandable_list_tag, null);
        Tag tag = sortedTags.get(groupPosition);
        ((TextView) groupView.findViewById(R.id.recyclerView_text))
                .setText(tag.name());
        CheckBox checkBox = groupView.findViewById(R.id.recyclerView_checkBox);
        checkBox.setChecked(tag.enabled() == 1);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ERDatabase.getInstance(context).enableAsync(tag);
            } else {
                ERDatabase.getInstance(context).disableAsync(tag);
            }
        });
        groupView.setOnClickListener(v -> {
            ExpandableListView expandableListView = (ExpandableListView) parent;
            if (expandableListView.isGroupExpanded(groupPosition)) {
                expandableListView.collapseGroup(groupPosition);
            } else {
                expandableListView.expandGroup(groupPosition);
            }
        });
        return groupView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = context.getSystemService(LayoutInflater.class);
        View childView = inflater.inflate(R.layout.expandable_list_exercise, null);
        Tag tag = sortedTags.get(groupPosition);
        Exercise exercise = taggedExercises.get(tag)
                .get(childPosition);
        ((TextView) childView.findViewById(R.id.recyclerView_text))
                .setText(exercise.name());
        CheckBox checkBox = childView.findViewById(R.id.recyclerView_checkBox);
        checkBox.setChecked(exercise.enabled() == 1);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ERDatabase.getInstance(context).enableAsync(exercise);
            } else {
                ERDatabase.getInstance(context).disableAsync(exercise);
            }
        });
        return childView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
