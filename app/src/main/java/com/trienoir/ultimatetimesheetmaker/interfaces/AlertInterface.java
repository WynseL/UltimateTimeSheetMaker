package com.trienoir.ultimatetimesheetmaker.interfaces;

import android.content.DialogInterface;

/**
 * Created by TrieNoir on 23/02/2016.
 */
public interface AlertInterface {
    void PositiveMethod(DialogInterface dialog, int id);
    void NegativeMethod(DialogInterface dialog, int id);
}
