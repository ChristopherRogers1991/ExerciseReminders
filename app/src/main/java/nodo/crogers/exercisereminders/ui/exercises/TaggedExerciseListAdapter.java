package nodo.crogers.exercisereminders.ui.exercises;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import nodo.crogers.exercisereminders.R;
import nodo.crogers.exercisereminders.database.ERDatabase;
import nodo.crogers.exercisereminders.database.entities.Exercise;
import nodo.crogers.exercisereminders.database.entities.Tag;

public class TaggedExerciseListAdapter extends BaseExpandableListAdapter {
    private final Map<Tag, List<Exercise>> taggedExercises;
    private final List<Tag> sortedTags;

    private final LifecycleOwner lifecycleOwner;

    public TaggedExerciseListAdapter(
            LifecycleOwner lifecycleOwner, Map<Tag, List<Exercise>> taggedExercises) {
        this.lifecycleOwner = lifecycleOwner;
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
        @SuppressLint("InflateParams") View groupView = inflater.inflate(R.layout.expandable_list_tag, null);
        Tag tag = sortedTags.get(groupPosition);
        ((TextView) groupView.findViewById(R.id.listVIew_exerciseNameText))
                .setText(tag.name());
        CheckBox checkBox = groupView.findViewById(R.id.listItem_exerciseEnabledCheckbox);
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
    @SuppressLint("InflateParams")
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = context.getSystemService(LayoutInflater.class);
        View childView = inflater.inflate(R.layout.expandable_list_exercise, null);
        Tag parentTag = sortedTags.get(groupPosition);
        Exercise exercise = taggedExercises.get(parentTag)
                .get(childPosition);
        ((TextView) childView.findViewById(R.id.listVIew_exerciseNameText))
                .setText(exercise.name());
        CheckBox checkBox = childView.findViewById(R.id.listItem_exerciseEnabledCheckbox);
        checkBox.setChecked(exercise.enabled() == 1);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ERDatabase.getInstance(context).enableAsync(exercise);
            } else {
                ERDatabase.getInstance(context).disableAsync(exercise);
            }
        });
        childView.setOnLongClickListener(v -> {
            View deleteDialogBody = inflater.inflate(R.layout.delete_dialog, null);
            TextView deleteConfirmation = deleteDialogBody.findViewById(R.id.delete_confirmation);
            deleteConfirmation.setText(context.getString(R.string.delete_exercise, exercise.name()));
            AlertDialog deleteDialog = new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.delete))
                    .setView(deleteDialogBody)
                    .setCancelable(true)
                    .setNegativeButton(R.string.cancel, (_dialog, _which) -> {})
                    .setPositiveButton(R.string.delete, ((dialog, which) -> {
                        ERDatabase.executorService.execute(() -> {
                            ERDatabase.getInstance(context).exerciseDao().delete(exercise);
                        });
                        dialog.dismiss();
                    }))
                    .create();
            deleteDialog.show();
            return true;
        });
        TextView disabledByText = childView.findViewById(R.id.listVIew_exerciseDisabledByText);
        disabledByText.setVisibility(View.GONE);
        ERDatabase.executorService.execute(() -> {
            LiveData<List<Tag>> exerciseTags = ERDatabase.getInstance(context)
                    .exerciseDao()
                    .getTags(exercise);
            disabledByText.post(() -> exerciseTags.observe(lifecycleOwner, tags -> {
                List<String> disabledTags = tags.stream()
                        .filter(tag -> tag.enabled() == 0)
                        .filter(tag -> tag.id() != parentTag.id())
                        .map(Tag::name)
                        .collect(Collectors.toList());
                disabledByText.post(() -> {
                    if (disabledTags.isEmpty()) {
                        disabledByText.setVisibility(View.GONE);
                    } else {
                        disabledByText.setText(
                                context.getResources()
                                        .getString(R.string.disabled_by,
                                                String.join(", ", disabledTags)));
                        disabledByText.setVisibility(View.VISIBLE);
                    }
                });
            }));
        });

        return childView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
