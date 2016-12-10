package com.abstractx1.mydiary.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.BaseExpandableListAdapter;

import com.abstractx1.mydiary.DataCollection;
import com.abstractx1.mydiary.MyDiaryActivity;
import com.abstractx1.mydiary.R;
import com.abstractx1.mydiary.Researcher;
import com.abstractx1.mydiary.record.RecordHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tfisher on 02/12/2016.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
        import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class QuestionsExpandableListAdapter extends BaseExpandableListAdapter {

    private MyDiaryActivity activity;
    private Map<Integer,RecordHandler> recordHandlers;
    private Map<Integer,ViewHolder> viewHolders;

    static class ViewHolder {
        public ImageButton recordButton;
        public ImageButton playButton;
        public ImageButton clearRecordingButton;
        public SeekBar recordingSeekBar;
        public TextView recordingDurationTextView;
        public TextView answer;
        public RecordHandler recordHandler;
    }

    public QuestionsExpandableListAdapter(MyDiaryActivity activity) {
        this.activity = activity;
        this.recordHandlers = new HashMap<>();
        this.viewHolders = new HashMap<>();
    }

    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null || recordHandlers.get(groupPosition) == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.questions_expandablelistview_child, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.recordButton = (ImageButton) convertView.findViewById(R.id.recordButtonChild);
            viewHolder.playButton = (ImageButton) convertView.findViewById(R.id.playButtonChild);
            viewHolder.clearRecordingButton = (ImageButton) convertView.findViewById(R.id.clearRecordingButtonChild);
            viewHolder.recordingSeekBar = (SeekBar) convertView.findViewById(R.id.recordingSeekBarChild);
            viewHolder.recordingDurationTextView = (TextView) convertView.findViewById(R.id.recordingDurationTextViewChild);
            viewHolder.answer = (TextView) convertView.findViewById(R.id.answerEditText);
            viewHolder.answer.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    Researcher.getInstance().getDataCollections().get(groupPosition).setAnswer((String) cs.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {}

                @Override
                public void afterTextChanged(Editable arg0) {}

            });
            try {
                RecordHandler recordHandler = new RecordHandler(activity,
                        viewHolder.recordButton,
                        viewHolder.playButton,
                        viewHolder.clearRecordingButton,
                        viewHolder.recordingSeekBar,
                        viewHolder.recordingDurationTextView,
                        Researcher.getInstance().getDataCollections().get(groupPosition));
                if (recordHandlers.get(groupPosition) == null) {
                    recordHandlers.put(groupPosition,recordHandler);
                }
                viewHolder.recordHandler = recordHandlers.get(groupPosition);
            } catch (Exception e) {
                activity.alert("Exception raised setting RecordHandler: " + e.getMessage());
            }
            this.viewHolders.put(groupPosition, viewHolder);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = this.viewHolders.get(groupPosition);
        }

        viewHolder.answer.setText((CharSequence) Researcher.getInstance().getDataCollections().get(groupPosition).getAnswer());

        viewHolder.answer.requestFocus();

        return convertView;
    }

    public List<ViewHolder> getViewHolders() {
        return this.getViewHolders();
    }

    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    public Object getGroup(int groupPosition) {
        return Researcher.getInstance().getDataCollections().get(groupPosition);
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
        String question = String.valueOf(groupPosition + 1) + ") " + dataCollection.getQuestion();
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
        ViewHolder viewHolder = viewHolders.get(groupPosition);

        if (viewHolder != null) {
            RecordHandler recordHandler = viewHolders.get(groupPosition).recordHandler;
            if(recordHandler.recordingInProgress()) {
                try {
                    recordHandler.cancelRecording();
                    activity.alert("Recording Cancelled");
                } catch (Exception e) {
                    activity.alert("Error cancelling recording: " + e.getMessage());
                }
            } else if (recordHandler.playingInProgress()) {
                try {
                    recordHandler.transitionTo(RecordHandler.PAUSED);
                    activity.alert("Recording playback paused");
                } catch (Exception e) {
                    activity.alert("Error pausing the recording playback: " + e.getMessage());
                }
            }
        }
    }
}