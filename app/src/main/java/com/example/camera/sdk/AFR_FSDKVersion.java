package com.example.camera.sdk;


public class AFR_FSDKVersion {
    String mVersion = null;
    long lFeatureLevel = 0L;

    public AFR_FSDKVersion() {
    }

    public String toString() {
        return this.mVersion;
    }

    public long getFeatureLevel() {
        return this.lFeatureLevel;
    }
}
