package com.calendlygui.model.entity;

<<<<<<< HEAD
=======
import com.calendlygui.model.Content;

>>>>>>> origin/UI
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

    public Meeting(int id, String name, Timestamp establishedDatetime, Timestamp occurDatetime, Timestamp finishDatetime, String classification,String selectedClassification, String status, List<Content> contents) {
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

=======
>>>>>>> origin/UI
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
