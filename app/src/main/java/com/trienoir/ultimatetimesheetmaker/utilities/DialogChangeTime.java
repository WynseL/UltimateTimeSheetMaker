package com.trienoir.ultimatetimesheetmaker.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.trienoir.ultimatetimesheetmaker.R;
import com.trienoir.ultimatetimesheetmaker.database.Attendance;
import com.trienoir.ultimatetimesheetmaker.enums.TimeFormat;
import com.trienoir.ultimatetimesheetmaker.interfaces.AlertInterface;
import com.trienoir.ultimatetimesheetmaker.interfaces.RefreshListInterface;

/**
 * Created by TrieNoir on 23/02/2016.
 */
public class DialogChangeTime {

    public void getConfirmDialog(Context mContext,
                                 final Attendance attendance,
                                        String positiveBtnCaption,
                                        String negativeBtnCaption,
                                        boolean isCancelable,
                                        final AlertInterface target) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.dialog_change_time, null);

        TextView text = (TextView) promptsView.findViewById(R.id.dialog_change_time_text);
        final CustomTimePicker timeIn = (CustomTimePicker) promptsView.findViewById(R.id.dialog_change_time_time_in);
        final CustomTimePicker timeOut = (CustomTimePicker) promptsView.findViewById(R.id.dialog_change_time_time_out);
        final EditText holiday = (EditText) promptsView.findViewById(R.id.dialog_change_time_holiday);
        Button timeInRemove = (Button) promptsView.findViewById(R.id.dialog_change_time_time_in_remove);
        Button timeOutRemove = (Button) promptsView.findViewById(R.id.dialog_change_time_time_out_remove);

        final CalendarTime calendarTime = new CalendarTime();
        calendarTime.initCalendar();

        text.setText(calendarTime.parseDateToExcel(attendance.getDate()));

        if (!calendarTime.isWeekend(attendance.getDate())) {
            if (!attendance.getTimeIn().isEmpty()) {
                String[] splitTimeIn = calendarTime.parseTimeTo24HR(attendance.getTimeIn()).split(":");
                timeIn.setCurrentHour(Integer.parseInt(splitTimeIn[0]));
                timeIn.setCurrentMinute(Integer.parseInt(splitTimeIn[1]));
            } else {
                timeIn.setCurrentHour(9);
                timeIn.setCurrentMinute(0);
            }

            if (!attendance.getTimeOut().isEmpty()) {
                String[] splitTimeOut = calendarTime.parseTimeTo24HR(attendance.getTimeOut()).split(":");
                timeOut.setCurrentHour(Integer.parseInt(splitTimeOut[0]));
                timeOut.setCurrentMinute(Integer.parseInt(splitTimeOut[1]));
            } else {
                timeOut.setCurrentHour(18);
                timeOut.setCurrentMinute(0);
            }
            holiday.setText(attendance.getHoliday());

            timeInRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseCommands.EditAttendance(
                            attendance,
                            "",
                            attendance.getTimeOut(),
                            attendance.getHoliday(),
                            new RefreshListInterface() {
                                @Override
                                public void onDatabaseUpdate(Attendance attendance) { target.PositiveMethod(null, 0); }
                            });
                }
            });

            timeOutRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseCommands.EditAttendance(
                            attendance,
                            attendance.getTimeIn(),
                            "",
                            attendance.getHoliday(),
                            new RefreshListInterface() {
                                @Override
                                public void onDatabaseUpdate(Attendance attendance) { target.PositiveMethod(null, 0); }
                            });
                }
            });
        } else {
            timeIn.setEnabled(false);
            timeIn.setCurrentHour(9);
            timeIn.setCurrentMinute(0);
            timeOut.setEnabled(false);
            timeOut.setCurrentHour(18);
            timeOut.setCurrentMinute(0);
            holiday.setEnabled(false);
            timeInRemove.setEnabled(false);
            timeOutRemove.setEnabled(false);
        }



        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(promptsView).setCancelable(false).setPositiveButton(positiveBtnCaption, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {

                if (!calendarTime.isWeekend(attendance.getDate())) {
                    if (holiday.getText().toString().trim().isEmpty()) {
                        DatabaseCommands.EditAttendance(
                                attendance,
                                calendarTime.parseTimeToAMPM(timeIn.getCurrentHour() + ":" + timeIn.getCurrentMinute() + ":00"),
                                calendarTime.parseTimeToAMPM(timeOut.getCurrentHour() + ":" + timeOut.getCurrentMinute() + ":00"),
                                "",
                                new RefreshListInterface() {
                                    @Override
                                    public void onDatabaseUpdate(Attendance attendance) {
                                        target.PositiveMethod(dialog, id);
                                    }
                                });
                    } else {
                        DatabaseCommands.EditAttendance(
                                attendance,
                                "",
                                "",
                                holiday.getText().toString(),
                                new RefreshListInterface() {
                                    @Override
                                    public void onDatabaseUpdate(Attendance attendance) {
                                        target.PositiveMethod(dialog, id);
                                    }
                                });
                    }
                } else {
                    target.PositiveMethod(dialog, id);
                }
                dialog.dismiss();

            }
        }).setNegativeButton(negativeBtnCaption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                target.NegativeMethod(dialog, id);
            }
        });

        final AlertDialog alert = builder.create();
        alert.setCancelable(isCancelable);
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }
}
