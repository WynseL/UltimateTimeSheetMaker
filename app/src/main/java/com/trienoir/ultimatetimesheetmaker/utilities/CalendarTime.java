package com.trienoir.ultimatetimesheetmaker.utilities;

import android.util.Log;

import com.trienoir.ultimatetimesheetmaker.enums.TimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by TrieNoir on 16/02/2016.
 */
public class CalendarTime {

    public static String dateTime = "yyyy-MM-dd HH:mm:ss";
    public static String date = "yyyy-MM-dd";
    public static String time = "hh:mm:ss a";
    public static String day = "EEEE";

    private Calendar calendar;
    public void initCalendar() {
        calendar = Calendar.getInstance();
    }

    public String getValue(TimeFormat format) {

        initCalendar();
        String strFormat = "";
        switch (format) {
            case DATE: strFormat = date; break;
            case TIME: strFormat = time; break;
            case DAY: strFormat = day; break;
            case DATETIME: strFormat = dateTime; break;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(strFormat);
        return dateFormat.format(calendar.getTime());
    }

    public String incrementDateByDay(String date, int increment) {
        try {
            String inputPattern = "yyyy-MM-dd";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            calendar.setTime(inputFormat.parse(date));
            calendar.add(Calendar.DATE, increment);
            return inputFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(Constants.TAG, e.toString());
            return null;
        }
    }

    public boolean isDateBefore(String strDate1, String strDate2) {
        String inputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = inputFormat.parse(strDate1);
            date2 = inputFormat.parse(strDate2);
            if (date1.before(date2)) { return true; }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isDateAfter(String strDate1, String strDate2) {
        String inputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = inputFormat.parse(strDate1);
            date2 = inputFormat.parse(strDate2);
            if (date1.after(date2)) { return true; }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String parseDateToExcel(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "EEEE, dd MMMM yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public boolean isWeekend(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "EEEE";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str.equals("Saturday") || str.equals("Sunday");
    }

    public String parseDateToMonth(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMMM";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String parseDateToDatabase(String time) {
        String inputPattern = "EEEE, dd MMMM yyyy";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String parseTimeToExcel(String time) {
        String inputPattern = "hh:mm:ss a";
        String outputPattern = "hh:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String parseTimeTo24HR(String time) {
        String inputPattern = "hh:mm:ss a";
        String outputPattern = "HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String parseTimeToAMPM(String time) {
        String inputPattern = "HH:mm:ss";
        String outputPattern = "hh:mm:ss a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public int getMonth() { return calendar.get(Calendar.MONTH); }
    public int getYear() { return calendar.get(Calendar.YEAR); }
    public int getDay() { return calendar.get(Calendar.DAY_OF_MONTH); }
}
