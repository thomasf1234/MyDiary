package com.abstractx1.mydiary.dialogs;

/**
 * Created by tfisher on 22/12/2016.
 */

import android.app.TimePickerDialog;
import android.widget.TimePicker;

import com.abstractx1.mydiary.GlobalApplicationValues;
import com.abstractx1.mydiary.MyDiaryActivity;
import com.abstractx1.mydiary.MyDiaryApplication;


/**
 * Created by tfisher on 27/10/2016.
 */

public class EditAlarmDialog {

    public static TimePickerDialog create(final MyDiaryActivity activity) {

        TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        GlobalApplicationValues.editNotificationHour(activity, hourOfDay);
                        GlobalApplicationValues.editNotificationMinute(activity, minute);
                        MyDiaryApplication.initializeAlarm(activity);
                    }
                }, GlobalApplicationValues.getNotificationHour(activity),
                GlobalApplicationValues.getNotificationMinute(activity), false);

        timePickerDialog.setTitle("Edit Reminder");

        return timePickerDialog;
    }
}


