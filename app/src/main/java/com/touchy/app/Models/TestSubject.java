package com.touchy.app.Models;

public class TestSubject {
    private String name, handingTechnique, screenResolution, screenDensity, date;
    private int sessionLengthInTouches;
    private long timeSpent;
    private boolean isHelpEnabled;
    public TestSubject(String name, String handingTechnique, String screenResolution, String screenDensity, String date, int sessionLengthInTouches) {
        this.name = name;
        this.handingTechnique = handingTechnique;
        this.screenResolution = screenResolution;
        this.screenDensity = screenDensity;
        this.date = date;
        this.sessionLengthInTouches = sessionLengthInTouches;
    }

    public String getName() {
        return name;
    }

    public String getHandingTechnique() {
        return handingTechnique;
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

    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public long getTimeSpent() {
        return timeSpent;
    }

    public boolean isHelpEnabled() {
        return isHelpEnabled;
    }

    public void setHelpEnabled(boolean helpEnabled) {
        isHelpEnabled = helpEnabled;
    }

    public String getScreenDensity() {
        return screenDensity;
    }
}
