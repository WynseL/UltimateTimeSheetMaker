package com.trienoir.ultimatetimesheetmaker.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;

import com.trienoir.ultimatetimesheetmaker.database.Attendance;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

/**
 * Created by TrieNoir on 22/02/2016.
 */
public class ReadWriteExcelFile {

    public static final String DIRECTORY_NAME = "ultimatetimesheetmaker";

    public static String EmailSubject = "";

    public static void readExcelFile(Context context, String fileLocation) throws IOException{

        CalendarTime calendarTime = new CalendarTime();
        calendarTime.initCalendar();

        InputStream ExcelFileToRead = new FileInputStream(fileLocation);
        HSSFWorkbook workbook = new HSSFWorkbook(ExcelFileToRead);

        Sheet sheet = workbook.getSheetAt(workbook.getSheetIndex("Sheet1"));
//        Sheet sheet = workbook.getSheetAt(0);
        Row row;
        Cell cell;

        Iterator rows = sheet.rowIterator();

        while (rows.hasNext()) {

            String date = null, timeIn = "", timeOut = "", holiday = "";

            row = (HSSFRow) rows.next();
            Iterator cells = row.cellIterator();

            while (cells.hasNext()) {

                cell = (HSSFCell) cells.next();

                switch (cell.getColumnIndex()) {
                    case 1:
                        date = calendarTime.parseDateToDatabase(GetDateTime(calendarTime, 1, cell));
                        break;
                    case 2:
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:
                                timeIn = "";
                                holiday = GetHoliday(cell);
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                timeIn = String.valueOf(GetDateTime(calendarTime, 2, cell));
                                holiday = "";
                                break;
                        }
                        break;
                    case 3:
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:
                                timeOut = "";
                                holiday = GetHoliday(cell);
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                timeOut = String.valueOf(GetDateTime(calendarTime, 2, cell));
                                holiday = "";
                                break;
                        }
                        break;
                    default: break;
                }
            }
            DatabaseCommands.AddAttendance(date, timeIn, timeOut, holiday);
        }
    }

    private static String GetHoliday(Cell cell) {
        String value = "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().getString();
                //Log.v("TAG", "value: " + value);
                break;
            default:
                value = ""; break;
        }
        return value;
    }

    private static String GetDateTime(CalendarTime calendarTime, int type, Cell cell) {
        String value = "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:

                if (DateUtil.isCellDateFormatted(cell)) {
                    if (type == 1) {
                        value = calendarTime.parseDateToExcel(String.valueOf(
                                (cell.getDateCellValue().getYear() + 1900) + "-" +
                                        (cell.getDateCellValue().getMonth() + 1) + "-" +
                                        cell.getDateCellValue().getDate()
                        ));
                        //Log.v("TAG", "value: " + value);
                    } else if (type == 2) {
                        value = calendarTime.parseTimeToAMPM(String.valueOf(
                                cell.getDateCellValue().getHours() + ":" +
                                        cell.getDateCellValue().getMinutes() + ":" +
                                        cell.getDateCellValue().getSeconds()
                        ));
                        //Log.v("TAG", "value: " + value);
                    }
                } else {
                    value = String.valueOf(cell.getNumericCellValue());
                }

                break;
        }
        return value;
    }

    public static void saveExcelFile(Context context, List<Attendance> attendanceList, String fileName) throws IOException{

        CalendarTime calendarTime = new CalendarTime();
        calendarTime.initCalendar();

        File directory = new File(Environment.getExternalStorageDirectory() + "/" + DIRECTORY_NAME + "/");
        File file = new File(directory, fileName + ".xls");

        if (!directory.exists()) { directory.mkdirs(); }
        //if (!file.exists()) { file.createNewFile(); }

        Workbook workbook = new HSSFWorkbook();

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        CellStyle cellStyleHoliday = workbook.createCellStyle();
        cellStyleHoliday.setFillForegroundColor(HSSFColor.RED.index);
        cellStyleHoliday.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyleHoliday.setAlignment(CellStyle.ALIGN_CENTER);

        CellStyle cellStyleWeekend = workbook.createCellStyle();
        cellStyleWeekend.setFillForegroundColor(HSSFColor.BROWN.index);
        cellStyleWeekend.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        CellStyle cellStyleCenter = workbook.createCellStyle();
        cellStyleCenter.setAlignment(CellStyle.ALIGN_CENTER);

        Sheet sheet1;
        sheet1 = workbook.createSheet("Sheet1");

        Row row;
        Cell cell;

        for(int i = 0; i < attendanceList.size();i++)
        {
            Attendance attendance = attendanceList.get(i);

            row = sheet1.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);

            cell = row.createCell(1);
            cell.setCellValue(attendance.getDate().isEmpty() ? "" : calendarTime.parseDateToExcel(attendance.getDate()));
            cell.setCellStyle(cellStyleCenter);
            CellStyle customCellStyle = calendarTime.isWeekend(attendance.getDate()) ? cellStyleWeekend : cellStyleCenter;

            if (attendance.getHoliday().isEmpty()) {
                cell = row.createCell(2);
                cell.setCellValue(attendance.getTimeIn().isEmpty() ? "" : calendarTime.parseTimeToExcel(attendance.getTimeIn()));
                cell.setCellStyle(customCellStyle);

                cell = row.createCell(3);
                cell.setCellValue(attendance.getTimeOut().isEmpty() ? "" : calendarTime.parseTimeToExcel(attendance.getTimeOut()));
                cell.setCellStyle(customCellStyle);
            } else {
                cell = row.createCell(2);
                cell.setCellValue(attendance.getHoliday());
                cell.setCellStyle(cellStyleHoliday);
                sheet1.addMergedRegion(new CellRangeAddress(i, i, 2, 3));
            }
        }

        sheet1.setColumnWidth(0, (15 * 250));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 250));
        sheet1.setColumnWidth(3, (15 * 250));

        FileOutputStream os = null;

        try { os = new FileOutputStream(file); workbook.write(os); }
        catch (IOException e) { e.printStackTrace(); }
        catch (Exception e) { e.printStackTrace(); }
        finally {
            try {
                if (null != os) {
                    os.close();
                }
                sendToEmail(context, file.getAbsolutePath());
            }
            catch (Exception ex) {  }
        }
    }

    private static void sendToEmail(Context context, String location) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("application/excel");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, Constants.TO_RECEIVER);
        emailIntent.putExtra(android.content.Intent.EXTRA_CC, Constants.CC_RECEIVER);
        emailIntent.putExtra(android.content.Intent.EXTRA_BCC, Constants.BCC_RECEIVER);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, EmailSubject);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, String.format(Constants.BODY, "[name]", "[start MMMM dd]", "[end MMMM dd]"));
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + location));

        final PackageManager pm = context.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches) {
            if (info.activityInfo.packageName.endsWith(".gm") ||
                    info.activityInfo.name.toLowerCase().contains("gmail")) { best = info; }
        }
        if (best != null) { emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name); }
        context.startActivity(emailIntent);
    }

//    public static boolean isExternalStorageReadOnly() {
//        String extStorageState = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
//            return true;
//        }
//        return false;
//    }
//
//    public static boolean isExternalStorageAvailable() {
//        String extStorageState = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
//            return true;
//        }
//        return false;
//    }

}
