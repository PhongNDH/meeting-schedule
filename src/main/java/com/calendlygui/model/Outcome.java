package com.calendlygui.model;

import com.calendlygui.model.entity.User;

import java.io.Serializable;

public class Outcome implements Serializable {
    User user;
    ErrorMessage error;

    public Outcome(User user) {
        this.user = user;
        this.error = null;
    }

    public Outcome(ErrorMessage error) {
        this.error = error;
        this.user = null;
    }

    public User getUser() {
        return user;
    }
    public ErrorMessage getError() {
        return error;
    }
}
