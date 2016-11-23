package com.abstractx1.mydiary.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.abstractx1.mydiary.AppInfo;
import com.abstractx1.mydiary.R;

import java.util.List;

/**
 * Created by tfisher on 22/11/2016.
 */

public class ApplicationArrayAdapter extends ArrayAdapter<AppInfo> {
    private Context context;
    private List<AppInfo> appInfos;
    private int selectedIndex;

    public ApplicationArrayAdapter(Context context, int resource, List<AppInfo> appInfos) {
        super(context, resource, appInfos);
        this.context = context;
        this.appInfos = appInfos;
        this.selectedIndex = -1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AppInfo appInfo = appInfos.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.email_client_listview_item, null);

        TextView name = (TextView) view.findViewById(R.id.emailClientListViewElementNameTextView);
        ImageView icon = (ImageView) view.findViewById(R.id.emailClientListViewElementIconImageView);
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.emailClientListViewElementRadiobutton);

        if(selectedIndex == position){
            radioButton.setChecked(true);
        }
        else{
            radioButton.setChecked(false);
        }

        name.setText(appInfo.getName());
        icon.setImageDrawable(appInfo.getIcon());

        return view;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index, ListView listView) {
        if(!isFirstTimeSelected()) {
            if(getSelectedIndex() >= listView.getFirstVisiblePosition() && getSelectedIndex() <= listView.getLastVisiblePosition()) {
                View previouslistViewElement = listView.getChildAt(getSelectedIndex() - listView.getFirstVisiblePosition());
                RadioButton previousSelectedRadioButton = (RadioButton) previouslistViewElement.findViewById(R.id.emailClientListViewElementRadiobutton);
                previousSelectedRadioButton.setChecked(false);
            }
        }
        this.selectedIndex = index;
        View currentlistViewElement = listView.getChildAt(getSelectedIndex() - listView.getFirstVisiblePosition());
        RadioButton currentSelectedRadioButton = (RadioButton) currentlistViewElement.findViewById(R.id.emailClientListViewElementRadiobutton);
        currentSelectedRadioButton.setChecked(true);
    }

    public boolean isFirstTimeSelected() {
        return selectedIndex == -1;
    }
}
