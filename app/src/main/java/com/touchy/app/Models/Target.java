package com.touchy.app.Models;

public class Target {
    private float x, y;
    private int radius;

    public Target(float x, float y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public Target() {
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
