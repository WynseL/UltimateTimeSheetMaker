package com.trienoir.ultimatetimesheetmaker.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.trienoir.ultimatetimesheetmaker.R;
import com.trienoir.ultimatetimesheetmaker.database.Attendance;
import com.trienoir.ultimatetimesheetmaker.interfaces.AlertInterface;
import com.trienoir.ultimatetimesheetmaker.interfaces.RefreshListInterface;

/**
 * Created by TrieNoir on 23/02/2016.
 */
public class DialogSetHoliday {

    public static void getConfirmDialog(Context mContext,
                                        String positiveBtnCaption,
                                        String negativeBtnCaption,
                                        boolean isCancelable,
                                        final AlertInterface target) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.dialog_set_holiday, null);

        final EditText text = (EditText) promptsView.findViewById(R.id.dialog_set_holiday_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(promptsView).setCancelable(false).setPositiveButton(positiveBtnCaption, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {

                DatabaseCommands.RegisterHoliday(text.getText().toString(), new RefreshListInterface() {
                    @Override
                    public void onDatabaseUpdate(Attendance attendance) {
                        target.PositiveMethod(dialog, id);
                    }
                });
            }
        }).setNegativeButton(negativeBtnCaption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                target.NegativeMethod(dialog, id);
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
}
