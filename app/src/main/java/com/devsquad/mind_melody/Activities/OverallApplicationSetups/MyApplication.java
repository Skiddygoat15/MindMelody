package com.devsquad.mind_melody.Activities.OverallApplicationSetups;

import android.app.Application;

import com.devsquad.mind_melody.Model.User.User;

public class MyApplication extends Application {
    private User loggedInUser;

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }
}