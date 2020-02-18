package com.example.camera.sdk;


import android.graphics.Rect;

public class AFT_FSDKFace {
    Rect mRect;
    int mAlarmType;
    int mAlarmState;
    int mDegree;

    public AFT_FSDKFace(AFT_FSDKFace self) {
        mRect = new Rect(self.getRect());
        mDegree = self.getDegree();
    }

    public AFT_FSDKFace() {
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

    public AFT_FSDKFace clone() {
        return new AFT_FSDKFace(this);
    }
}