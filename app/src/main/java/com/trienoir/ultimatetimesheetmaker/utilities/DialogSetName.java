package com.trienoir.ultimatetimesheetmaker.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.trienoir.ultimatetimesheetmaker.R;
import com.trienoir.ultimatetimesheetmaker.database.Attendance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by TrieNoir on 23/02/2016.
 */
public class DialogSetName {

    public static void getConfirmDialog(final Context mContext,
                                        final List<Attendance> list,
                                        String positiveBtnCaption,
                                        String negativeBtnCaption,
                                        boolean isCancelable) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.dialog_set_name, null);

        final TextView chad = (TextView) promptsView.findViewById(R.id.dialog_set_name_chad);
        final Spinner month = (Spinner) promptsView.findViewById(R.id.dialog_set_name_month);
        final Spinner part = (Spinner) promptsView.findViewById(R.id.dialog_set_name_part);
        final Spinner year = (Spinner) promptsView.findViewById(R.id.dialog_set_name_year);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, getYears(list));
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, getMonths(list));

        year.setAdapter(yearAdapter);
        month.setAdapter(monthAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(promptsView).setCancelable(false).setPositiveButton(positiveBtnCaption, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String fileName = chad.getText().toString() +
                        month.getSelectedItem().toString() +
                        part.getSelectedItem().toString() +
                        year.getSelectedItem().toString();

                ReadWriteExcelFile.EmailSubject = month.getSelectedItem().toString() + " " +
                        part.getSelectedItem().toString().toLowerCase() + " " +
                        year.getSelectedItem().toString();

                dialog.dismiss();

                try { ReadWriteExcelFile.saveExcelFile(mContext, list, fileName); }
                catch (IOException e) { e.printStackTrace(); }
            }
        }).setNegativeButton(negativeBtnCaption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        final AlertDialog alert = builder.create();
        alert.setCancelable(isCancelable);
        alert.show();
        if (isCancelable) {
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface arg0) {
                    alert.dismiss();
                }
            });
        }
    }

    private static List<String> getYears(List<Attendance> attendances) {
        List<String> yearList = new ArrayList<>();
        for(Attendance attendance : attendances) {
            String[] splitYear = attendance.getDate().split("-");
            yearList.add(splitYear[0]);
        }
        Set<String> set = new LinkedHashSet<>(yearList);
        yearList.clear();
        yearList.addAll(set);
        return yearList;
    }

    private static List<String> getMonths(List<Attendance> attendances) {
        CalendarTime calendarTime = new CalendarTime();
        List<String> monthList = new ArrayList<>();
        for(Attendance attendance : attendances) {
            String month = calendarTime.parseDateToMonth(attendance.getDate());
            monthList.add(month);
        }
        Set<String> set = new LinkedHashSet<>(monthList);
        monthList.clear();
        monthList.addAll(set);
        return monthList;
    }

}
