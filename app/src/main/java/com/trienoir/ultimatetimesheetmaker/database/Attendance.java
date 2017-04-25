package com.trienoir.ultimatetimesheetmaker.database;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "ATTENDANCE".
 */
@Entity
public class Attendance {

    @Id
    private Long id;
    private String date;
    private String timeIn;
    private String timeOut;
    private String holiday;
    private java.util.Date createdAt;

    @Generated
    public Attendance() {
    }

    public Attendance(Long id) {
        this.id = id;
    }

    @Generated
    public Attendance(Long id, String date, String timeIn, String timeOut, String holiday, java.util.Date createdAt) {
        this.id = id;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.holiday = holiday;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }

    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }

}
