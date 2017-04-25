package com.trienoir.ultimatetimesheetmaker.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.trienoir.ultimatetimesheetmaker.R;
import com.trienoir.ultimatetimesheetmaker.interfaces.AlertInterface;


public class DialogYesNo {
	
	public static void getConfirmDialog(Context mContext,
                                        String title,
                                        String msg,
                                        String positiveBtnCaption,
                                        String negativeBtnCaption,
                                        boolean isCancelable,
                                        final AlertInterface target) {
		LayoutInflater li = LayoutInflater.from(mContext);
		View promptsView = li.inflate(R.layout.dialog_yes_no, null);
		
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
        
        TextView Title = (TextView) promptsView.findViewById(R.id.tvDialogTitle);
        TextView Message = (TextView) promptsView.findViewById(R.id.tvMsg);
        
        Title.setText(title);
        Message.setText(msg);

        final AlertDialog alert = builder.create();
        //alert.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alert.setCancelable(isCancelable);
        alert.show();
        if (isCancelable) {
            alert.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface arg0) {
                    	alert.dismiss();
                }
            });
        }
    }
}
