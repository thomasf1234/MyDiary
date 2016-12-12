package com.abstractx1.mydiary.adapters;

import android.widget.BaseExpandableListAdapter;

import com.abstractx1.mydiary.DataCollection;
import com.abstractx1.mydiary.MyDiaryActivity;
import com.abstractx1.mydiary.R;
import com.abstractx1.mydiary.Researcher;
import com.abstractx1.mydiary.record.RecordHandler;
import com.abstractx1.mydiary.views.QuestionsExpandableListViewChild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

/**
 * Created by tfisher on 02/12/2016.
 */

public class QuestionsExpandableListAdapter extends BaseExpandableListAdapter {

    private MyDiaryActivity activity;

    public QuestionsExpandableListAdapter(MyDiaryActivity activity) {
        this.activity = activity;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        QuestionsExpandableListViewChild questionsExpandableListViewChild = new QuestionsExpandableListViewChild(activity, Researcher.getInstance().getDataCollection(groupPosition+1));
        return questionsExpandableListViewChild.getView();
    }

    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    public Object getGroup(int groupPosition) {
        return Researcher.getInstance().getDataCollection(groupPosition + 1);
    }

    public int getGroupCount() {
        return Researcher.getInstance().getDataCollections().size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        DataCollection dataCollection = (DataCollection) getGroup(groupPosition);
        String question = dataCollection.getQuestionNumber() + ") " + dataCollection.getQuestion();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.questions_expandablelistview_group,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.question);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(question);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void haltGroup(int groupPosition) {
        //questionsExpandableListViewChildren.get(groupPosition).halt();
    }
}