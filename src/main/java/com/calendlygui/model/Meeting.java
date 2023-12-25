package com.calendlygui.model;

import java.sql.Timestamp;
import java.util.List;

public class Meeting {

    public Meeting(int id, int teacherId, String name, Timestamp establishedDatetime, Timestamp occurDatetime, Timestamp finishDatetime, String classification) {
        this.id = id;
        this.teacherId = teacherId;
        this.name = name;
        this.establishedDatetime = establishedDatetime;
        this.occurDatetime = occurDatetime;
        this.finishDatetime = finishDatetime;
        this.classification = classification;
    }

    int id;

    int teacherId;
    //String teacherName;

    String name;

    Timestamp establishedDatetime;

    Timestamp occurDatetime;

    Timestamp finishDatetime;

    String classification;

    String selectedClassification;

    String status;

    List<Content> contents;

    public int getId() {
        return id;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public String getName() {
        return name;
    }

    public Timestamp getEstablishedDatetime() {
        return establishedDatetime;
    }

    public Timestamp getOccurDatetime() {
        return occurDatetime;
    }

    public Timestamp getFinishDatetime() {
        return finishDatetime;
    }

    public String getClassification() {
        return classification;
    }

    public String getSelectedClassification() {
        return selectedClassification;
    }

    public String getStatus() {
        return status;
    }

    public List<Content> getContents() {
        return contents;
    }
}
