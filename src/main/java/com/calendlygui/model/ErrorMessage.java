package com.calendlygui.model;

import java.io.Serializable;

public class ErrorMessage implements Serializable {
    String content;

    public ErrorMessage() {
    }
    public ErrorMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
