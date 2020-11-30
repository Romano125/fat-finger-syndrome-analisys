package com.touchy.app.Models;

public class StatisticsData {
    private String touchedArea;
    private int numberOfTouches;
    private double averageError, averageSize, averagePressure;

    public StatisticsData(String touchedArea, int numberOfTouches, double averageError, double averageSize, double averagePressure) {
        this.touchedArea = touchedArea;
        this.numberOfTouches = numberOfTouches;
        this.averageError = averageError;
        this.averageSize = averageSize;
        this.averagePressure = averagePressure;
    }
}
