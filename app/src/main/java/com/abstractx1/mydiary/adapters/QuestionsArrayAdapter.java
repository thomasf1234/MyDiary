package com.abstractx1.mydiary.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.abstractx1.mydiary.DataCollection;
import com.abstractx1.mydiary.R;

import java.util.List;

/**
 * Created by tfisher on 22/11/2016.
 */

public class QuestionsArrayAdapter extends ArrayAdapter<DataCollection> {
    private Context context;
    private List<DataCollection> dataCollections;

    public QuestionsArrayAdapter(Context context, int resource, List<DataCollection> dataCollections) {
        super(context, resource, dataCollections);
        this.context = context;
        this.dataCollections = dataCollections;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        DataCollection dataCollection = dataCollections.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.questions_listview_item, parent, false);

        TextView questionTextView = (TextView) view.findViewById(R.id.questionsListViewElementQuestionTextView);

        String question = dataCollection.getQuestionNumber() + ") " + dataCollection.getQuestion();
        questionTextView.setText(question);

        return view;
    }
}
