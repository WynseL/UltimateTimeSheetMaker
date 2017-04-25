package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class GreenDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.trienoir.ultimatetimesheetmaker.database");

        Entity attendance = schema.addEntity("Attendance");
        attendance.addIdProperty();
        attendance.addStringProperty("date");
        attendance.addStringProperty("timeIn");
        attendance.addStringProperty("timeOut");
        attendance.addStringProperty("holiday");
        attendance.addDateProperty("createdAt");

        new DaoGenerator().generateAll(schema, "../app/src/main/java");
    }
}
