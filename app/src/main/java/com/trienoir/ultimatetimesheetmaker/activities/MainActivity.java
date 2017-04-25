package com.trienoir.ultimatetimesheetmaker.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.trienoir.ultimatetimesheetmaker.R;
import com.trienoir.ultimatetimesheetmaker.utilities.CalendarTime;
import com.trienoir.ultimatetimesheetmaker.utilities.ReadWriteExcelFile;
import com.trienoir.ultimatetimesheetmaker.views.AttendanceView;

import java.io.IOException;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private TextView currentTime, currentDay, currentDate;
    private Button tapToGo;
    FrameLayout attendanceLayout;
    AttendanceView attendanceView;
    private CalendarTime calendarTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentTime = (TextView) findViewById(R.id.currentTime);
        currentDay = (TextView) findViewById(R.id.currentDay);
        currentDate = (TextView) findViewById(R.id.currentDate);
        tapToGo = (Button) findViewById(R.id.tapToGo);

        int value = 0;
        if (getIntent().hasExtra("timeIn")) { value = 1; }
        else if (getIntent().hasExtra("open")) { value = 0; }
        else if (getIntent().hasExtra("holiday")) { value = 3; }
        else if (getIntent().hasExtra("timeOut")) { value = 2; }

        attendanceLayout = (FrameLayout) findViewById(R.id.attendance_sheet_view);
        attendanceView = new AttendanceView(MainActivity.this, value);
        attendanceLayout.addView(attendanceView);

        calendarTime = new CalendarTime();

        tapToGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        String path = getPath(this, uri);
                        Log.v("TAG", "path: " + path);
                        ReadWriteExcelFile.readExcelFile(MainActivity.this, path);
                    }
                    catch (URISyntaxException | IOException e) { e.printStackTrace(); }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //refreshTime.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //refreshTime.onFinish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void openCalendarDialog(int year, int month, int day) {
        DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        }, year, month, day);
        dialog.show();
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try { startActivityForResult(Intent.createChooser(intent, "Select excel file"), 0); }
        catch (android.content.ActivityNotFoundException ex) { }
    }

    public String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            }
            catch (Exception e) { }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private Integer intParser(String s) { return  Integer.parseInt(s); }
}
