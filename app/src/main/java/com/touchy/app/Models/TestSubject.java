package com.touchy.app.Models;

public class TestSubject {
    private String name, handedness, screenResolution, date;
    private int sessionLengthInTouches;

    public TestSubject(String name, String handedness, String screenResolution, String date, int sessionLengthInTouches) {
        this.name = name;
        this.handedness = handedness;
        this.screenResolution = screenResolution;
        this.date = date;
        this.sessionLengthInTouches = sessionLengthInTouches;
    }

    public String getName() {
        return name;
    }

    public String getHandedness() {
        return handedness;
    }

    public String getScreenResolution() {
        return screenResolution;
    }

    public String getDate() {
        return date;
    }

    public int getSessionLengthInTouches() {
        return sessionLengthInTouches;
    }
}
