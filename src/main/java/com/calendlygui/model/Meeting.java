package com.calendlygui.model;

import java.util.ArrayList;

public class Meeting {
    public int id;
    public String name;
    public String date;
    public String occur;
    public String finish;
    public int tId;
    public String classification;
    public String status;
    public String selectedClassification;

    public ArrayList<Minute> minutes = new ArrayList<>();

    // Constructor
    public Meeting(int id, String name, String date, String occur, String finish,
                   int tId, String classification, String status, String selectedClassification) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.occur = occur;
        this.finish = finish;
        this.tId = tId;
        this.classification = classification;
        this.status = status;
        this.selectedClassification = selectedClassification;
    }

//    public void addMinute(Minute minute){
//        minutes.add(minute);
//    }

    @Override
    public String toString() {
        return "Meeting{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", occur='" + occur + '\'' +
                ", finish='" + finish + '\'' +
                ", tId=" + tId +
                ", classification='" + classification + '\'' +
                ", status='" + status + '\'' +
                ", selectedClassification='" + selectedClassification + '\'' +
                ", Minutes: " + minutes.size() +
                '}';
    }
}

