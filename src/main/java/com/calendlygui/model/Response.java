package com.calendlygui.model;

import java.io.Serializable;
import java.time.LocalTime;

public class Response implements Serializable {
    int code;   //0: Success    1: Client message error     2: Server error     3: SQL error
    LocalTime time;
    int uid;
    Object body;

    //getters
    public int getCode() {
        return this.code;
    }

    public int getUid() {
        return uid;
    }

    public Object getBody() {
        return body;
    }

    public LocalTime getTime() {
        return time;
    }

    //constructors
    public Response(int code, Object body) {
        this.code = code;
        this.time = LocalTime.now();
        this.body = body;
    }

    @Override
    public String toString() {
        return code + " - " + uid + " - " + body;
    }
}