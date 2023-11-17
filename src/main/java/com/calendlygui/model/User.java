package com.calendlygui.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class User implements Serializable {
    String username;
    String email;
    Timestamp registerDatetime;
    boolean isTeacher;
    boolean gender;
    String mode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getRegisterDatetime() {
        return registerDatetime;
    }

    public void setRegisterDatetime(Timestamp registerDatetime) {
        this.registerDatetime = registerDatetime;
    }

    public boolean isTeacher() {
        return isTeacher;
    }

    public void setTeacher(boolean teacher) {
        isTeacher = teacher;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public User() {
    }

    public User(String username, String email, Timestamp registerDatetime, boolean isTeacher, boolean gender, String mode) {
        this.username = username;
        this.email = email;
        this.registerDatetime = registerDatetime;
        this.isTeacher = isTeacher;
        this.gender = gender;
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "Name : "+ username + "\n"+
                "Email : "+email +"\n"+
                "Register Datetime : "+registerDatetime+"\n"+
                "Gender : " + (gender ? "Male" : "Female")+ "\n"+
                "Occupation : " + (isTeacher ? "Teacher" : "Student");
    }
}
