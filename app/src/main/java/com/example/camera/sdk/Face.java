package com.example.camera.sdk;


import android.graphics.Rect;

public class Face {
    private Rect mRect;
    private int mAlarmType;
    private int mAlarmState;
    private int mDegree;

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

    public int getAlarmType() {
        return mAlarmType;
    }

    public void setAlarmType(int mAlarmType) {
        this.mAlarmType = mAlarmType;
    }

    public int getAlarmState() {
        return mAlarmState;
    }

    public void setAlarmState(int mAlarmState) {
        this.mAlarmState = mAlarmState;
    }

    public String toString() {
        return this.mRect.toString() + "," + this.mDegree;
    }

    public Face clone() {
        return new Face(this);
    }
}