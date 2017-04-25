package com.trienoir.ultimatetimesheetmaker.interfaces;

import com.trienoir.ultimatetimesheetmaker.database.Attendance;

/**
 * Created by TrieNoir on 21/02/2016.
 */
public interface RefreshListInterface {
    void onDatabaseUpdate(Attendance attendance);
}
