package com.touchy.app.Models;

public class Touch {
    private double x;
    private double y;

    public Touch(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Touch() {
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
