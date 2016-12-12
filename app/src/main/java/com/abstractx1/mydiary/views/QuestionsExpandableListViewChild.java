package com.abstractx1.mydiary.views;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.abstractx1.mydiary.DataCollection;
import com.abstractx1.mydiary.MyDiaryActivity;
import com.abstractx1.mydiary.R;
import com.abstractx1.mydiary.Researcher;
import com.abstractx1.mydiary.record.RecordHandler;

/**
 * Created by tfisher on 12/12/2016.
 */

public class QuestionsExpandableListViewChild {
    private MyDiaryActivity activity;
    private DataCollection dataCollection;
    private View view;
    private ViewHolder viewHolder;
    private RecordHandler recordHandler;

    static class ViewHolder {
        public ImageButton recordButton;
        public ImageButton playButton;
        public ImageButton clearRecordingButton;
        public SeekBar recordingSeekBar;
        public TextView recordingDurationTextView;
        public EditText answer;
    }

    public QuestionsExpandableListViewChild(MyDiaryActivity activity, DataCollection dataCollection) {
        this.activity = activity;
        this.dataCollection = dataCollection;
        initializeView();
    }

    private void initializeView() {
        LayoutInflater inflater = activity.getLayoutInflater();
        this.view = inflater.inflate(R.layout.questions_expandablelistview_child, null);
        this.viewHolder = new ViewHolder();
        viewHolder.recordButton = (ImageButton) view.findViewById(R.id.recordButtonChild);
        viewHolder.playButton = (ImageButton) view.findViewById(R.id.playButtonChild);
        viewHolder.clearRecordingButton = (ImageButton) view.findViewById(R.id.clearRecordingButtonChild);
        viewHolder.recordingSeekBar = (SeekBar) view.findViewById(R.id.recordingSeekBarChild);
        viewHolder.recordingDurationTextView = (TextView) view.findViewById(R.id.recordingDurationTextViewChild);
        viewHolder.answer = (EditText) view.findViewById(R.id.answerEditText);
        viewHolder.answer.setText((CharSequence) dataCollection.getAnswer());
        viewHolder.answer.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                dataCollection.setAnswer((String) cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }

        });

        try {
            this.recordHandler = new RecordHandler(activity,
                    viewHolder.recordButton,
                    viewHolder.playButton,
                    viewHolder.clearRecordingButton,
                    viewHolder.recordingSeekBar,
                    viewHolder.recordingDurationTextView,
                    dataCollection);
        } catch (Exception e) {
            activity.alert("Exception raised setting RecordHandler: " + e.getMessage());
        }
    }

    public View getView() {
        return view;
    }

    public View getAnswer() {
        return viewHolder.answer;
    }

    public void halt() {
        if (recordHandler.recordingInProgress()) {
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
