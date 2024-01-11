package com.calendlygui.model.entity;

import com.calendlygui.utils.Format;

import java.sql.Timestamp;
import java.util.ArrayList;
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

    public Meeting(int id, int teacherId, String teacherName, String name, Timestamp establishedDatetime, Timestamp occurDatetime, Timestamp finishDatetime, String classification, String status, String selectedClassification) {
        this.id = id;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.name = name;
        this.establishedDatetime = establishedDatetime;
        this.occurDatetime = occurDatetime;
        this.finishDatetime = finishDatetime;
        this.classification = classification;
        this.selectedClassification = selectedClassification;
        this.status = status;
    }



    public Meeting(int id, int teacherId, String name, Timestamp establishedDatetime, Timestamp occurDatetime, Timestamp finishDatetime, String selectedClassification, String status) {
        this.id = id;
        this.teacherId = teacherId;
        this.name = name;
        this.establishedDatetime = establishedDatetime;
        this.occurDatetime = occurDatetime;
        this.finishDatetime = finishDatetime;
        this.selectedClassification = selectedClassification;
        this.status = status;
    }

    public Meeting(int id, String name, Timestamp establishedDatetime, Timestamp occurDatetime, Timestamp finishDatetime, String classification, String selectedClassification, String status, List<Content> contents) {
        this.id = id;
        this.name = name;
        this.establishedDatetime = establishedDatetime;
        this.occurDatetime = occurDatetime;
        this.finishDatetime = finishDatetime;
        this.selectedClassification = selectedClassification;
        this.classification = classification;
        this.status = status;
        this.contents = contents;
    }

    public Meeting(int id, int teacherId, String name, Timestamp establishedDatetime, Timestamp occurDatetime, Timestamp finishDatetime, String classification, String selectedClassification, String status) {
        this.id = id;
        this.teacherId = teacherId;
        this.name = name;
        this.establishedDatetime = establishedDatetime;
        this.occurDatetime = occurDatetime;
        this.finishDatetime = finishDatetime;
        this.selectedClassification = selectedClassification;
        this.classification = classification;
        this.status = status;
    }


    public Meeting(int id, String name, Timestamp establishedDatetime, Timestamp occurDatetime, Timestamp finishDatetime, String selectedClassification, String status, List<Content> contents) {
        this.id = id;
        this.name = name;
        this.establishedDatetime = establishedDatetime;
        this.occurDatetime = occurDatetime;
        this.finishDatetime = finishDatetime;
        this.selectedClassification = selectedClassification;
        this.status = status;
        this.contents = contents;
    }

    int id;

    int teacherId;

    String teacherName;

    String name;

    Timestamp establishedDatetime;

    Timestamp occurDatetime;

    Timestamp finishDatetime;

    String classification;

    String selectedClassification;

    String status;

    List<Content> contents = new ArrayList<>();

    List<User> students = new ArrayList<>();

    public int getId() {
        return id;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public String getName() {
        return name;
    }

    public List<User> getStudents() {
        return students;
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

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public void setStudents(List<User> students) {
        this.students = students;
    }

    public String getTeacherName() {
        return teacherName;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date='" + Format.getStringFormatFromTimestamp(occurDatetime, "yyyy-MM-dd") + '\'' +
                ", occur='" + Format.getStringFormatFromTimestamp(occurDatetime, "HH:mm") + '\'' +
                ", finish='" + Format.getStringFormatFromTimestamp(finishDatetime, "HH:mm") + '\'' +
                ", teacherId=" + teacherId +
                ", teacherName=" + teacherName +
                ", classification='" + classification + '\'' +
                ", status='" + status + '\'' +
                ", selectedClassification='" + selectedClassification + '\'' +
                ", establishTime='" + Format.getStringFormatFromTimestamp(establishedDatetime, "yyyy-MM-dd HH:mm") + '\'' +
                ", Contents: " + contents.size() +
                ", Students: " + students.size() +
                '}';
    }
}
