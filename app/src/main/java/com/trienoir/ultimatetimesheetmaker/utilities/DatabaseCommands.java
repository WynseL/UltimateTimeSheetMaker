package com.trienoir.ultimatetimesheetmaker.utilities;

import android.content.Context;
import android.util.Log;

import com.trienoir.ultimatetimesheetmaker.database.Attendance;
import com.trienoir.ultimatetimesheetmaker.database.AttendanceDao;
import com.trienoir.ultimatetimesheetmaker.database.DaoMaster;
import com.trienoir.ultimatetimesheetmaker.database.DaoSession;
import com.trienoir.ultimatetimesheetmaker.enums.TimeFormat;
import com.trienoir.ultimatetimesheetmaker.interfaces.RefreshListInterface;

import java.util.Date;
import java.util.List;

/**
 * Created by TrieNoir on 17/02/2016.
 */
public class DatabaseCommands {

    static DaoSession daoSession;
    static CalendarTime calendarTime;
    static Context context;

    public static void InitDao(Context _context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(_context, "attendance-db", null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        daoSession = daoMaster.newSession();
        calendarTime = new CalendarTime();
        context = _context;
    }

    public static void AddAttendance(
            String date,
            String timeIn,
            String timeOut,
            String holiday) {
        Attendance attendance = new Attendance();
        attendance.setDate(date);
        attendance.setTimeIn(timeIn);
        attendance.setTimeOut(timeOut);
        attendance.setHoliday(holiday);
        attendance.setCreatedAt(new Date());
        AttendanceDao attendanceDao = daoSession.getAttendanceDao();
        attendanceDao.insertOrReplace(attendance);
    }

    public static void EditAttendance(
            Attendance attendance,
            String timeIn,
            String timeOut,
            String holiday,
            RefreshListInterface refreshListInterface) {
        attendance.setTimeIn(timeIn);
        attendance.setTimeOut(timeOut);
        attendance.setHoliday(holiday);
        AttendanceDao attendanceDao = daoSession.getAttendanceDao();
        attendanceDao.update(attendance);
        if (refreshListInterface != null) refreshListInterface.onDatabaseUpdate(attendance);
    }

    public static void DeleteAttendance(
            Attendance attendance,
            RefreshListInterface refreshListInterface) {
        AttendanceDao attendanceDao = daoSession.getAttendanceDao();
        attendanceDao.delete(attendance);
        if (refreshListInterface != null) refreshListInterface.onDatabaseUpdate(attendance);
    }

    public static Attendance ReadCurrentAttendance() {
        AttendanceDao attendanceDao = daoSession.getAttendanceDao();
        return attendanceDao.loadByRowId(GetDateToday().get(0).getId());
    }

    public static void RegisterTimeIn(RefreshListInterface refreshListInterface) {
        AddDefaultAttendance();

        Attendance attendance = ReadCurrentAttendance();
        if (attendance.getTimeIn().isEmpty() && attendance.getHoliday().isEmpty()) {
            attendance.setTimeIn(calendarTime.getValue(TimeFormat.DAY).equals("Saturday") ||
                    calendarTime.getValue(TimeFormat.DAY).equals("Sunday") ? "" : calendarTime.getValue(TimeFormat.TIME));
            AttendanceDao attendanceDao = daoSession.getAttendanceDao();
            attendanceDao.update(attendance);
        }
        if (refreshListInterface != null) refreshListInterface.onDatabaseUpdate(attendance);
    }

    public static void RegisterTimeOut(RefreshListInterface refreshListInterface) {
        AddDefaultAttendance();

        Attendance attendance = ReadCurrentAttendance();
        if (attendance.getTimeOut().isEmpty() && attendance.getHoliday().isEmpty()) {
            attendance.setTimeOut(calendarTime.getValue(TimeFormat.DAY).equals("Saturday") ||
                    calendarTime.getValue(TimeFormat.DAY).equals("Sunday") ? "" : calendarTime.getValue(TimeFormat.TIME));
            AttendanceDao attendanceDao = daoSession.getAttendanceDao();
            attendanceDao.update(attendance);
        }
        if (refreshListInterface != null) refreshListInterface.onDatabaseUpdate(attendance);
    }

    public static void RegisterHoliday(String holiday, RefreshListInterface refreshListInterface) {
        AddDefaultAttendance();

        Attendance attendance = ReadCurrentAttendance();
        //if (attendance.getHoliday().equals("") || attendance.getHoliday().isEmpty()) {
            attendance.setTimeIn("");
            attendance.setTimeOut("");
            attendance.setHoliday(calendarTime.getValue(TimeFormat.DAY).equals("Saturday") ||
                    calendarTime.getValue(TimeFormat.DAY).equals("Sunday") ? "" : holiday);
            AttendanceDao attendanceDao = daoSession.getAttendanceDao();
            attendanceDao.update(attendance);
        //}
        if (refreshListInterface != null) refreshListInterface.onDatabaseUpdate(attendance);
    }

    public static List<Attendance> ReadAllAttendance() {
        AttendanceDao attendanceDao = daoSession.getAttendanceDao();
        return attendanceDao.loadAll();
    }

    public static boolean CheckIfDateExistsOnDB() {
        List<Attendance> attendances = ReadAllAttendance();
        for (Attendance attendance : attendances) {
            if (attendance.getDate().equals(calendarTime.getValue(TimeFormat.DATE))) { return true; }
        }
        return false;
    }

    public static void AddAllNotAddedAttendanceDate() {
        List<Attendance> attendances = ReadAllAttendance();
        if (!attendances.isEmpty()) {
            if (!CheckIfDateExistsOnDB()) {
                Attendance attendance = attendances.get(attendances.size() - 1);
                String lastAddedDate = attendance.getDate();
                String currentDate = calendarTime.getValue(TimeFormat.DATE);
                if (calendarTime.isDateAfter(currentDate, lastAddedDate)) {
                    int increment = 0;
                    boolean isLastAddedAndCurrentDateEqual;
                    do {
                        increment++;
                        String addedDate = calendarTime.incrementDateByDay(lastAddedDate, increment);
                        AddAttendance(addedDate, "", "", "");
                        isLastAddedAndCurrentDateEqual = currentDate.equals(addedDate);
                    }
                    while (!isLastAddedAndCurrentDateEqual);
                }
            }
        } else {
            AddDefaultAttendance();
        }
    }

    public static void AddDefaultAttendance(RefreshListInterface refreshListInterface) {
        if (!CheckIfDateExistsOnDB()) {
            if (calendarTime.getValue(TimeFormat.DAY).equals("Saturday") ||
                    calendarTime.getValue(TimeFormat.DAY).equals("Sunday")) {
                AddAttendance(calendarTime.getValue(TimeFormat.DATE), "", "", "");
            }
            else { AddAttendance(calendarTime.getValue(TimeFormat.DATE), "", "", ""); }
        }
        if (refreshListInterface != null) refreshListInterface.onDatabaseUpdate(ReadCurrentAttendance());
    }

    public static void AddDefaultAttendance() {
        if (!CheckIfDateExistsOnDB()) {
            if (calendarTime.getValue(TimeFormat.DAY).equals("Saturday") ||
                    calendarTime.getValue(TimeFormat.DAY).equals("Sunday")) {
                AddAttendance(calendarTime.getValue(TimeFormat.DATE), "", "", "");
            } else { AddAttendance(calendarTime.getValue(TimeFormat.DATE), "", "", ""); }
        }
    }

    public static Attendance GetLastAttendance() {
        AttendanceDao attendanceDao = daoSession.getAttendanceDao();
        List<Attendance> attendances = attendanceDao.queryBuilder()
                .limit(1)
                .orderDesc(AttendanceDao.Properties.Id)
                .list();
        return attendances.get(0);
    }

    public static List<Attendance> GetSortDateResult(String start, String end) {
        AttendanceDao attendanceDao = daoSession.getAttendanceDao();
        List<Attendance> attendances = attendanceDao.queryBuilder()
                .where(AttendanceDao.Properties.Date.between(start, end))
                .list();

        Log.v("ss", "start: " + start + ", end: " + end);
        Log.v("ss", "attendances: " + attendances);

        return attendances;
    }

    public static List<Attendance> GetAscendingDateResult() {
        AttendanceDao attendanceDao = daoSession.getAttendanceDao();
        List<Attendance> attendances = attendanceDao.queryBuilder()
                .orderAsc(AttendanceDao.Properties.Date)
                .list();

        return attendances;
    }

    public static List<Attendance> GetDescendingDateResult() {
        AttendanceDao attendanceDao = daoSession.getAttendanceDao();
        List<Attendance> attendances = attendanceDao.queryBuilder()
                .orderDesc(AttendanceDao.Properties.Date)
                .list();

        return attendances;
    }

    public static List<Attendance> GetDateToday() {
        AttendanceDao attendanceDao = daoSession.getAttendanceDao();
        List<Attendance> attendances = attendanceDao.queryBuilder()
                .where(AttendanceDao.Properties.Date.eq(calendarTime.getValue(TimeFormat.DATE)))
                .list();

        return attendances;
    }

    public static List<Attendance> GetMonthlySort() {
        AttendanceDao attendanceDao = daoSession.getAttendanceDao();

        String latestTime = calendarTime.getValue(TimeFormat.DATE);
        int lastIndex = latestTime.lastIndexOf("-");
        String getYearAndMonth = latestTime.substring(0, lastIndex);

        List<Attendance> attendances = attendanceDao.queryBuilder()
                .where(AttendanceDao.Properties.Date.like(getYearAndMonth + "%"))
                .list();

        return attendances;
    }
}
