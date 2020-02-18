package com.example.camera.sdk;


import android.graphics.Rect;

public class Face {
    Rect mRect;
    int mAlarmType;
    int mAlarmState;
    int mDegree;

    public Face(Face self) {
        mRect = new Rect(self.getRect());
        mDegree = self.getDegree();
    }

    public Face() {
        mRect = new Rect();
        mDegree = 0;
    }

    public Rect getRect() {
        return this.mRect;
    }

    public int getDegree() {
        return this.mDegree;
    }

    public String toString() {
        return this.mRect.toString() + "," + this.mDegree;
    }

    public Face clone() {
        return new Face(this);
    }
}