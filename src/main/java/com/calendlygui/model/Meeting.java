package com.calendlygui.model;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Meeting {
    public int id;
    public String name;
    public Timestamp establishDate;
    public Timestamp occur;
    public Timestamp finish;
    public int tId;
    public String classification;
    public String status;
    public String selectedClassification;

    public ArrayList<Minute> minutes = new ArrayList<>();
    public ArrayList<User> students = new ArrayList<>();

    // Constructor
    public Meeting(int id, String name, Timestamp date, Timestamp occur, Timestamp finish,
                   int tId, String classification, String status, String selectedClassification) {
        this.id = id;
        this.name = name;
        this.establishDate = date;
        this.occur = occur;
        this.finish = finish;
        this.tId = tId;
        this.classification = classification;
        this.status = status;
        this.selectedClassification = selectedClassification;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date='" + establishDate + '\'' +
                ", occur='" + occur + '\'' +
                ", finish='" + finish + '\'' +
                ", tId=" + tId +
                ", classification='" + classification + '\'' +
                ", status='" + status + '\'' +
                ", selectedClassification='" + selectedClassification + '\'' +
                ", Minutes: " + minutes.size() +
                ", Students: " + students.size() +
                '}';
    }
}

