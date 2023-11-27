package com.calendlygui.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Request implements Serializable {
    int uid;
    String method;
    ArrayList<String> body;
    public Request(String method){
        this.method = method;
    }

    public Request(String method, ArrayList<String> body) {
        this.method = method;
        this.body = body;
    }

    public Request(int uid, String method, ArrayList<String> body) {
        this.uid = uid;
        this.method = method;
        this.body = body;
    }

    public int getUid() {
        return uid;
    }

    public String getMethod() {
        return method;
    }

    public ArrayList<String> getBody() {
        return body;
    }

    @Override
    public String toString() {
        StringBuilder bodyString = new StringBuilder();
        for(String data: body) bodyString.append(" ").append(data);
        return uid + " - " + method + " -" + bodyString;
    }
}
