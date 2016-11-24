package com.abstractx1.mydiary.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import com.abstractx1.mydiary.MainMenuItem;
import com.abstractx1.mydiary.R;

import java.util.List;

/**
 * Created by tfisher on 22/11/2016.
 */

public class MainMenuArrayAdapter extends ArrayAdapter<MainMenuItem> {
    private Context context;
    private List<MainMenuItem> mainMenuItems;

    public MainMenuArrayAdapter(Context context, int resource, List<MainMenuItem> mainMenuItems) {
        super(context, resource, mainMenuItems);
        this.context = context;
        this.mainMenuItems = mainMenuItems;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        MainMenuItem mainMenuItem = mainMenuItems.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.main_menu_listview_item, null);

        TextView name = (TextView) view.findViewById(R.id.mainMenuListViewElementLabelTextView);
        ImageView icon = (ImageView) view.findViewById(R.id.mainMenuListViewElementIconImageView);

        name.setText(mainMenuItem.getLabel());
        icon.setImageDrawable(mainMenuItem.getIcon());

        return view;
    }
}
