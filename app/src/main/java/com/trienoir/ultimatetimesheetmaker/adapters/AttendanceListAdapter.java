package com.trienoir.ultimatetimesheetmaker.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trienoir.ultimatetimesheetmaker.R;
import com.trienoir.ultimatetimesheetmaker.database.Attendance;
import com.trienoir.ultimatetimesheetmaker.enums.TimeFormat;
import com.trienoir.ultimatetimesheetmaker.utilities.CalendarTime;
import com.trienoir.ultimatetimesheetmaker.utilities.DatabaseCommands;

import java.util.List;

/**
 * Created by TrieNoir on 16/02/2016.
 */
public class AttendanceListAdapter extends BaseAdapter {

    Context context;
    List<Attendance> attendances;
    CalendarTime calendarTime;

    private class ViewHolder {
        LinearLayout llContainer;
        TextView txtDate;
        LinearLayout llTimeContainer;
        TextView txtTimeIn;
        TextView txtTimeOut;
        TextView txtHoliday;
    }

    public AttendanceListAdapter(Context context, List<Attendance> attendances, CalendarTime calendarTime) {
        this.context = context;
        this.attendances = attendances;
        this.calendarTime = calendarTime;
    }

    @Override
    public int getCount() {
        return attendances.size();
    }

    @Override
    public Object getItem(int position) {
        return attendances.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_attendancesheet, parent, false);
            viewHolder.llContainer = (LinearLayout) convertView.findViewById(R.id.item_container);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.item_date);
            viewHolder.llTimeContainer = (LinearLayout) convertView.findViewById(R.id.item_time_container);
            viewHolder.txtTimeIn = (TextView) convertView.findViewById(R.id.item_time_in);
            viewHolder.txtTimeOut = (TextView) convertView.findViewById(R.id.item_time_out);
            viewHolder.txtHoliday = (TextView) convertView.findViewById(R.id.item_holiday);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Attendance attendance = attendances.get(position);

        if (attendance.getDate().equals(calendarTime.getValue(TimeFormat.DATE))) {
            viewHolder.llContainer.setBackgroundColor(context.getResources().getColor(R.color.colorHighlight));
        }
        else { viewHolder.llContainer.setBackgroundColor(0); }

        if (!calendarTime.isWeekend(attendance.getDate())) {
            if (!attendance.getHoliday().isEmpty()) {
                viewHolder.llTimeContainer.setVisibility(View.GONE);
                viewHolder.txtHoliday.setVisibility(View.VISIBLE);

                viewHolder.txtDate.setText(attendance.getDate().isEmpty() ? "" : calendarTime.parseDateToExcel(attendance.getDate()));
                viewHolder.txtHoliday.setText(attendance.getHoliday());
                viewHolder.txtHoliday.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
            } else {
                viewHolder.llTimeContainer.setVisibility(View.VISIBLE);
                viewHolder.txtHoliday.setVisibility(View.GONE);

                viewHolder.txtDate.setText(attendance.getDate().isEmpty() ? "" : calendarTime.parseDateToExcel(attendance.getDate()));
                viewHolder.txtTimeIn.setText(attendance.getTimeIn().isEmpty() || !attendance.getTimeIn().contains(":") ? "" : calendarTime.parseTimeToExcel(attendance.getTimeIn()));
                viewHolder.txtTimeOut.setText(attendance.getTimeOut().isEmpty() || !attendance.getTimeIn().contains(":") ? "" : calendarTime.parseTimeToExcel(attendance.getTimeOut()));
            }
        } else {
            viewHolder.llTimeContainer.setVisibility(View.GONE);
            viewHolder.txtHoliday.setVisibility(View.VISIBLE);

            viewHolder.txtDate.setText(attendance.getDate().isEmpty() ? "" : calendarTime.parseDateToExcel(attendance.getDate()));
            viewHolder.txtHoliday.setText("");
            viewHolder.txtHoliday.setBackgroundColor(context.getResources().getColor(R.color.colorBrown));
        }

        return convertView;
    }
}
