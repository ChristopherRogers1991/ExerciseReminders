package nodo.crogers.exercisereminders.ui.exercises;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import nodo.crogers.exercisereminders.R;
import nodo.crogers.exercisereminders.database.ERDatabase;
import nodo.crogers.exercisereminders.database.Exercise;
import nodo.crogers.exercisereminders.database.Tag;

public class TaggedExerciseListAdapter extends BaseExpandableListAdapter {
    private final LiveData<Map<Tag, List<Exercise>>> taggedExercises;
    private Map<Tag, List<Exercise>> current;
    private List<Tag> sortedTags;

    public TaggedExerciseListAdapter(LiveData<Map<Tag, List<Exercise>>> taggedExercises) {
        this.taggedExercises = taggedExercises;
        this.current = new HashMap<>();
        this.taggedExercises.observeForever(map -> {
            sortedTags = map.keySet()
                    .stream()
                    .sorted(Comparator.comparing(Tag::name))
                    .collect(Collectors.toList());
            sortedTags.sort(Comparator.comparing(Tag::name));
            current = map;
        });
    }

    @Override
    public int getGroupCount() {
        return current.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return current.get(sortedTags.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return sortedTags.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return current.get(sortedTags.get(groupPosition)).get(childPosition);
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
        Exercise exercise = current.get(tag)
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
