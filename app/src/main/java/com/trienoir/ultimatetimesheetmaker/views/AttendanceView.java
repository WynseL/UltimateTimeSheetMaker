package com.trienoir.ultimatetimesheetmaker.views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.trienoir.ultimatetimesheetmaker.R;
import com.trienoir.ultimatetimesheetmaker.activities.MainActivity;
import com.trienoir.ultimatetimesheetmaker.adapters.AttendanceListAdapter;
import com.trienoir.ultimatetimesheetmaker.database.Attendance;
import com.trienoir.ultimatetimesheetmaker.interfaces.AlertInterface;
import com.trienoir.ultimatetimesheetmaker.interfaces.RefreshListInterface;
import com.trienoir.ultimatetimesheetmaker.utilities.CalendarTime;
import com.trienoir.ultimatetimesheetmaker.utilities.DatabaseCommands;
import com.trienoir.ultimatetimesheetmaker.utilities.DialogChangeTime;
import com.trienoir.ultimatetimesheetmaker.utilities.DialogSetHoliday;
import com.trienoir.ultimatetimesheetmaker.utilities.DialogSetName;

import java.util.List;

/**
 * Created by TrieNoir on 16/02/2016.
 */
public class AttendanceView extends FrameLayout {

    private ListView attendanceList;
    private AttendanceListAdapter listAdapter;
    private Button startDate, endDate, sortDate, exportDate;

    private CalendarTime calendarTime;

    private List<Attendance> attendances;
    private int value;

    boolean isAscending = true;

    public AttendanceView(Context context, int value) {
        super(context);

        View view = View.inflate(getContext(), R.layout.activity_attendancesheet, this);
        this.value = value;

        init(view);
    }

    private void init(View view) {

        attendanceList = (ListView) view.findViewById(R.id.attendance_sheet_view_list);

        startDate = (Button) view.findViewById(R.id.attendance_sheet_view_start);
        endDate = (Button) view.findViewById(R.id.attendance_sheet_view_end);
        sortDate = (Button) view.findViewById(R.id.attendance_sheet_view_sort);
        exportDate = (Button) view.findViewById(R.id.attendance_sheet_view_export);

        calendarTime = new CalendarTime();
        calendarTime.initCalendar();

        DatabaseCommands.InitDao(getContext());
        attendances = DatabaseCommands.GetMonthlySort();
        DatabaseCommands.AddAllNotAddedAttendanceDate();
        switch (value) {
            case 1:
                DatabaseCommands.RegisterTimeIn(new RefreshListInterface() {
                    @Override
                    public void onDatabaseUpdate(Attendance attendance) {
                        attendances = DatabaseCommands.GetMonthlySort();
                        initListAndAdapter();
                        showToast(getContext(), "SET TIME_IN TO: " + attendance.getTimeIn());
                    }
                });
                break;
            case 2:
                DatabaseCommands.RegisterTimeOut(new RefreshListInterface() {
                    @Override
                    public void onDatabaseUpdate(Attendance attendance) {
                        attendances = DatabaseCommands.GetMonthlySort();
                        initListAndAdapter();
                        showToast(getContext(), "SET TIME_OUT TO: " + attendance.getTimeOut());
                    }
                });
                break;
            case 3:
                DialogSetHoliday.getConfirmDialog(
                        getContext(),
                        "Ok",
                        "Cancel",
                        false,
                        new AlertInterface() {
                            @Override
                            public void PositiveMethod(DialogInterface dialog, int id) {
                                attendances = DatabaseCommands.GetMonthlySort();
                                initListAndAdapter();
                            }

                            @Override
                            public void NegativeMethod(DialogInterface dialog, int id) {
                                attendances = DatabaseCommands.GetMonthlySort();
                                initListAndAdapter();
                            }
                        });

                break;
            default:
//                DatabaseCommands.AddAttendance("2017-01-01", "09:00 AM", "06:00 PM", "");


                attendances = DatabaseCommands.GetMonthlySort();
                initListAndAdapter();
                break;
        }

        attendanceList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                int getPosition = position - 1;
                Attendance attendance = attendances.get(getPosition);

                DialogChangeTime dialogChangeTime = new DialogChangeTime();
                dialogChangeTime.getConfirmDialog(getContext(),
                        attendance,
                        "Ok",
                        "Cancel",
                        false,
                        new AlertInterface() {
                            @Override
                            public void PositiveMethod(DialogInterface dialog, int id) { refreshList(); }

                            @Override
                            public void NegativeMethod(DialogInterface dialog, int id) { refreshList(); }
                        });

                return true;
            }
        });

        startDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String newDate = year + "-" +
                                        String.format("%02d", monthOfYear + 1) + "-" +
                                        String.format("%02d", dayOfMonth);
                                startDate.setText(newDate);
//                                if (endDate.getText().toString().equals("---")) {
//                                    String newDate2 = year + "-" + String.format("%02d", monthOfYear + 2) + "-" + 15;
//                                    endDate.setText(newDate2);
//                                }
                            }
                        },
                        calendarTime.getYear(),
                        calendarTime.getMonth(),
                        calendarTime.getDay());
                dialog.show();
            }
        });

        endDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int intYear, intMonth, intDay;
                if (endDate.getText().toString().equals("---")) {
                    intYear = calendarTime.getYear();
                    intMonth = calendarTime.getMonth();
                    intDay = calendarTime.getDay();
                } else {
                    String[] splitDate = endDate.getText().toString().split("-");
                    intYear = Integer.parseInt(splitDate[0]);
                    intMonth = Integer.parseInt(splitDate[1]) - 1;
                    intDay =  Integer.parseInt(splitDate[2]);
                }

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String newDate = year + "-" +
                                        String.format("%02d", monthOfYear + 1) + "-" +
                                        String.format("%02d", dayOfMonth);
                                endDate.setText(newDate);
                            }
                        },
                        intYear,
                        intMonth,
                        intDay);
                dialog.show();
            }
        });

        sortDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!startDate.getText().toString().equals("---") ||
                        !startDate.getText().toString().equals("---")) {
                    attendances = DatabaseCommands.GetSortDateResult(startDate.getText().toString(), endDate.getText().toString());
                } else {
                    if (isAscending) {
                        attendances = DatabaseCommands.GetDescendingDateResult();
                        isAscending = false;
                    } else {
                        attendances = DatabaseCommands.GetAscendingDateResult();
                        isAscending = true;
                    }
                }
                initListAndAdapter();
            }
        });

        exportDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!startDate.getText().toString().equals("---") ||
                        !startDate.getText().toString().equals("---")) {

                    attendances = DatabaseCommands.GetSortDateResult(startDate.getText().toString(), endDate.getText().toString());

                    DialogSetName.getConfirmDialog(
                            getContext(),
                            attendances,
                            "Ok",
                            "Cancel",
                            false);
                }
            }
        });

    }

    public void refreshList() {
        listAdapter.notifyDataSetChanged();
        attendanceList.invalidateViews();
    }

    public void initListAndAdapter() {
        listAdapter = new AttendanceListAdapter(getContext(), attendances, calendarTime);

        if (attendanceList.getHeaderViewsCount() == 0) {
            View header = ((MainActivity) getContext()).getLayoutInflater().inflate(R.layout.item_header_attendancesheet, null);
            attendanceList.addHeaderView(header);
        }
        attendanceList.setAdapter(listAdapter);
    }

    private void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }


}
