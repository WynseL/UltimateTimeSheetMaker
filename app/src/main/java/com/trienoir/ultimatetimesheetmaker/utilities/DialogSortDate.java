package com.trienoir.ultimatetimesheetmaker.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;

import com.trienoir.ultimatetimesheetmaker.R;
import com.trienoir.ultimatetimesheetmaker.interfaces.AlertInterface;

/**
 * Created by TrieNoir on 22/02/2016.
 */
public class DialogSortDate {

    public static void getConfirmDialog(Context mContext,
                                        String title,
                                        String msg,
                                        String positiveBtnCaption,
                                        String negativeBtnCaption,
                                        boolean isCancelable,
                                        final AlertInterface target) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.dialog_sort_by_date, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(promptsView).setCancelable(false).setPositiveButton(positiveBtnCaption, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                target.PositiveMethod(dialog, id);
            }
        }).setNegativeButton(negativeBtnCaption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                target.NegativeMethod(dialog, id);
            }
        });

        CalendarView calendarView = (CalendarView) promptsView.findViewById(R.id.dialog_sort_by_date_calendar);

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
}
