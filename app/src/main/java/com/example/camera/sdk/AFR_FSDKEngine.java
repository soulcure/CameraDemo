package com.example.camera.sdk;


import android.graphics.Rect;

public class AFR_FSDKEngine {
    private final String TAG = this.getClass().toString();
    public static final int CP_PAF_NV21 = 2050;
    public static final int AFR_FOC_0 = 1;
    public static final int AFR_FOC_90 = 2;
    public static final int AFR_FOC_270 = 3;
    public static final int AFR_FOC_180 = 4;
    public static final int AFR_FOC_30 = 5;
    public static final int AFR_FOC_60 = 6;
    public static final int AFR_FOC_120 = 7;
    public static final int AFR_FOC_150 = 8;
    public static final int AFR_FOC_210 = 9;
    public static final int AFR_FOC_240 = 10;
    public static final int AFR_FOC_300 = 11;
    public static final int AFR_FOC_330 = 12;
    private Integer handle = 0;
    private AFR_FSDKError error = new AFR_FSDKError();

    private native int FR_Init(String var1, String var2, AFR_FSDKError var3);

    private native int FR_UnInit(int var1);

    private native int FR_Registe(int var1, byte[] var2, int var3, int var4, int var5, Rect var6, int var7);

    private native int FR_GetFeatures(int var1, byte[] var2);

    private native int FR_GetErrorCode(int var1);

    private native float FR_Recognize(int var1, byte[] var2, byte[] var3);

    private native int FR_UpdateFeature(int var1, byte[] var2, byte[] var3);

    private native int FR_Version(int var1, AFR_FSDKVersion var2);

    public AFR_FSDKEngine() {
    }

    public AFR_FSDKError AFR_FSDK_InitialEngine(String appid, String sdkkey) {
        this.handle = this.FR_Init(appid, sdkkey, this.error);
        return this.error;
    }

    public AFR_FSDKError AFR_FSDK_UninitialEngine() {
        if (this.handle != 0) {
            this.error.mCode = this.FR_UnInit(this.handle);
            this.handle = 0;
        } else {
            this.error.mCode = 5;
        }

        return this.error;
    }

    public AFR_FSDKError AFR_FSDK_ExtractFRFeature(byte[] data, int width, int height, int format, Rect face, int ori, AFR_FSDKFace feature) {
        if (feature != null && data != null) {
            if (feature.mFeatureData == null) {
                this.error.mCode = 2;
            } else if (feature.mFeatureData.length < 22020) {
                this.error.mCode = 2;
            } else if (this.handle != 0) {
                int size = this.FR_Registe(this.handle, data, width, height, format, face, ori);
                this.error.mCode = this.FR_GetErrorCode(this.handle);
                if (size > 0 && this.error.mCode == 0) {
                    this.error.mCode = this.FR_GetFeatures(this.handle, feature.mFeatureData);
                }
            } else {
                this.error.mCode = 5;
            }
        } else {
            this.error.mCode = 2;
        }

        return this.error;
    }

    public AFR_FSDKError AFR_FSDK_FacePairMatching(AFR_FSDKFace ref, AFR_FSDKFace input, AFR_FSDKMatching score) {
        if (ref != null && input != null && score != null) {
            if (this.handle != 0) {
                score.mScore = this.FR_Recognize(this.handle, ref.mFeatureData, input.mFeatureData);
                this.error.mCode = this.FR_GetErrorCode(this.handle);
            } else {
                this.error.mCode = 5;
            }
        } else if (this.error != null) {
            this.error.mCode = 2;
        }

        return this.error;
    }

    private AFR_FSDKError AFR_FSDK_UpdateFeature(AFR_FSDKFace in, AFR_FSDKFace out) {
        if (in != null && out != null) {
            if (this.handle != 0) {
                this.error.mCode = this.FR_UpdateFeature(this.handle, in.mFeatureData, out.mFeatureData);
            } else {
                this.error.mCode = 5;
            }
        } else {
            this.error.mCode = 2;
        }

        return this.error;
    }

    public AFR_FSDKError AFR_FSDK_GetVersion(AFR_FSDKVersion version) {
        if (this.handle != 0) {
            this.error.mCode = this.FR_Version(this.handle, version);
        } else {
            this.error.mCode = 5;
        }

        return this.error;
    }

    static {
        //System.loadLibrary("mpbase");
        //System.loadLibrary("ArcSoft_FREngine");
    }
}
