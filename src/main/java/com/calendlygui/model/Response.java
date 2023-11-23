package com.calendlygui.model;

import java.io.Serializable;
import java.time.LocalTime;

public class Response implements Serializable {
    int code;   //0: Success    1: Client message error     2: Server error     3: SQL error
    LocalTime time;
    User user;
    ErrorMessage error;

    //getters
    public int getCode(){ return this.code; }
    public User getUser() { return user; }
    public ErrorMessage getError() { return error; }
    public LocalTime getTime() { return time; }

    //constructors
    public Response(int code, LocalTime time, User user){
        this.code = code;
        this.time = time;
        this.user = user;
    }

    public Response(int code, LocalTime time, ErrorMessage error){
        this.code = code;
        this.time = time;
        this.error = error;
    }
}