package com.touchy.app.Models;

import java.util.HashMap;

public class StatisticsData {
    private String touchedArea;
    private int numberOfTouches;
    private double averageError, averageSize, averagePressure;
    private HashMap<String, Integer> touchedAreas = new HashMap<>();
    private HashMap<String, Double> touchedAreaAverageError = new HashMap<>();
    private HashMap<String, Double> touchedAreaAverageSize = new HashMap<>();
    private HashMap<String, Double> touchedAreaAveragePressure = new HashMap<>();

    public StatisticsData(String touchedArea, int numberOfTouches, double averageError, double averageSize, double averagePressure) {
        this.touchedArea = touchedArea;
        this.numberOfTouches = numberOfTouches;
        this.averageError = averageError;
        this.averageSize = averageSize;
        this.averagePressure = averagePressure;
    }

    public StatisticsData() {
    }

    public String getTouchedArea() {
        return touchedArea;
    }

    public int getNumberOfTouches() {
        return numberOfTouches;
    }

    public double getAverageError() {
        return averageError;
    }

    public double getAverageSize() {
        return averageSize;
    }

    public double getAveragePressure() {
        return averagePressure;
    }

    public void setTouchedArea(String touchedArea) {
        this.touchedArea = touchedArea;
    }

    public void setNumberOfTouches(int numberOfTouches) {
        this.numberOfTouches = numberOfTouches;
    }

    public void setAverageError(double averageError) {
        this.averageError = averageError;
    }

    public void setAverageSize(double averageSize) {
        this.averageSize = averageSize;
    }

    public void setAveragePressure(double averagePressure) {
        this.averagePressure = averagePressure;
    }

    public HashMap<String, Integer> getTouchedAreas() {
        return touchedAreas;
    }

    public void setTouchedAreas(HashMap<String, Integer> touchedAreas) {
        this.touchedAreas = touchedAreas;
    }

    public HashMap<String, Double> getTouchedAreaAverageError() {
        return touchedAreaAverageError;
    }

    public void setTouchedAreaAverageError(HashMap<String, Double> touchedAreaAverageError) {
        this.touchedAreaAverageError = touchedAreaAverageError;
    }

    public HashMap<String, Double> getTouchedAreaAverageSize() {
        return touchedAreaAverageSize;
    }

    public void setTouchedAreaAverageSize(HashMap<String, Double> touchedAreaAverageSize) {
        this.touchedAreaAverageSize = touchedAreaAverageSize;
    }

    public HashMap<String, Double> getTouchedAreaAveragePressure() {
        return touchedAreaAveragePressure;
    }

    public void setTouchedAreaAveragePressure(HashMap<String, Double> touchedAreaAveragePressure) {
        this.touchedAreaAveragePressure = touchedAreaAveragePressure;
    }
}
