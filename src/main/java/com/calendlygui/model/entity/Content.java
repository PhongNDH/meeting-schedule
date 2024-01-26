package com.calendlygui.model.entity;

import java.sql.Timestamp;

public class Content {
    public Content(int id, int meetingId, String content, Timestamp date) {
        this.id = id;
        this.meetingId = meetingId;
        this.content = content;
        this.date = date;
    }

    public Content(String content, Timestamp date) {
        this.content = content;
        this.date = date;
    }

    int id;

    int meetingId;

    String content;

    Timestamp date;

    public int getMeetingId() {
        return meetingId;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getDate() {
        return date;
    }

    public int getId() {
        return id;
    }
}
