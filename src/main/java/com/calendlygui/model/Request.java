package com.calendlygui.model;

import java.util.List;

public class Request {
    int uid;
    String method;
    List<String> body;

    public Request(String method, List<String> body) {
        this.method = method;
        this.body = body;
    }

    public Request(int uid, String method, List<String> body) {
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

    public List<String> getBody() {
        return body;
    }

    @Override
    public String toString() {
        String bodyString = "";
        for(String data: body) bodyString += " " + data;
        return uid + " - " + method + " -" + bodyString;
    }
}
