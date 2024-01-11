package com.calendlygui.model;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class Minute {
    public Timestamp time;
    public String content;

    public Minute(Timestamp time, String content){
        this.time = time;
        this.content = content;
    }
}
