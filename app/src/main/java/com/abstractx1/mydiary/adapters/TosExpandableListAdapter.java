package com.abstractx1.mydiary.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.abstractx1.mydiary.MyDiaryActivity;
import com.abstractx1.mydiary.R;

import java.util.List;

/**
 * Created by tfisher on 09/01/2017.
 */

public class TosExpandableListAdapter extends BaseExpandableListAdapter {
    private MyDiaryActivity activity;
    private List<String> expandableListSection;
    private List<String> expandableListTitle;

    public TosExpandableListAdapter(MyDiaryActivity activity, List<String> expandableListTitle, List<String> expandableListSection) {
        this.activity = activity;
        this.expandableListTitle = expandableListTitle;
        this.expandableListSection = expandableListSection;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return this.expandableListSection.get(groupPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition;
    }

    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.list_item, null);
        TextView item = (TextView) convertView.findViewById(R.id.expandedListItem);
        float currentInputTextSize = item.getTextSize();
        float newInputTextSize = currentInputTextSize * 0.8f;
        item.setTextSize(TypedValue.COMPLEX_UNIT_PX, newInputTextSize);
        item.setText(this.expandableListSection.get(groupPosition));
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    public Object getGroup(int groupPosition) {
        return this.expandableListTitle.get(groupPosition);
    }

    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.list_group, null);
        TextView item = (TextView) convertView.findViewById(R.id.listTitle);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(this.expandableListTitle.get(groupPosition));
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}