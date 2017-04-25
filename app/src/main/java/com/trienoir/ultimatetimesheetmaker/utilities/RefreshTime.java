package com.trienoir.ultimatetimesheetmaker.utilities;

import android.os.Handler;

import com.trienoir.ultimatetimesheetmaker.enums.TimeFormat;
import com.trienoir.ultimatetimesheetmaker.interfaces.RefreshTimeInterface;

/**
 * Created by TrieNoir on 16/02/2016.
 */
public class RefreshTime {


    private static int refreshTimeInMillSec = 250;
    RefreshTimeInterface refreshTimeInterface;
    public RefreshTimeInterface getRefreshTimeInterface() { return refreshTimeInterface; }
    public void setRefreshTimeInterface(RefreshTimeInterface refreshTimeInterface) { this.refreshTimeInterface = refreshTimeInterface; }

    Handler timeUpdateHandler = new Handler();
    Runnable timeUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            refreshTimeInterface.onRefresh();
            onStart();
        }
    };

    public void onStart() { timeUpdateHandler.postDelayed(timeUpdateRunnable, refreshTimeInMillSec); }

    public void onFinish() { timeUpdateHandler.removeCallbacks(timeUpdateRunnable); }
}
