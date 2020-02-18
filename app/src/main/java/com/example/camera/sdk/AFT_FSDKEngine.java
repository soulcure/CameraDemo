package com.example.camera.sdk;


import java.util.List;

public class AFT_FSDKEngine {
    private final String TAG = this.getClass().toString();
    public static final int CP_PAF_NV21 = 2050;
    public static final int AFT_OPF_0_ONLY = 1;
    public static final int AFT_OPF_90_ONLY = 2;
    public static final int AFT_OPF_270_ONLY = 3;
    public static final int AFT_OPF_180_ONLY = 4;
    public static final int AFT_OPF_0_HIGHER_EXT = 5;
    public static final int AFT_FOC_0 = 1;
    public static final int AFT_FOC_90 = 2;
    public static final int AFT_FOC_270 = 3;
    public static final int AFT_FOC_180 = 4;
    private int result = 0;
    private AFT_FSDKError error = new AFT_FSDKError();
    private AFT_FSDKFace[] mFaces = new AFT_FSDKFace[16];
    private int mFaceCount = 0;

    private native int RD_Init(byte[] path);

    private native int RD_Process(byte[] data, int width, int height, int channel);

    private native int RD_Result(AFT_FSDKFace face);

    private native int FT_Config(int var1, int var2);


    private native int FT_GetErrorCode(int var1);

    private native int FT_UnInit(int var1);

    private native String FT_Version(int var1);

    public AFT_FSDKEngine() {
    }

    private AFT_FSDKFace[] obtainFaceArray(int size) {
        if (this.mFaceCount < size) {
            if (this.mFaces.length < size) {
                this.mFaces = new AFT_FSDKFace[(size / 16 + 1) * 16];
            }

            for (int i = this.mFaceCount; i < size; ++i) {
                this.mFaces[i] = new AFT_FSDKFace();
            }

            this.mFaceCount = size;
        }

        return this.mFaces;
    }

    public AFT_FSDKError RD_InitialFaceEngine() {
        //todo
        result = RD_Init("test path".getBytes());
        return this.error;
    }

    public AFT_FSDKError AFT_FSDK_FaceFeatureDetect(byte[] data, int width, int height, int channel, List<AFT_FSDKFace> list) {
        if (list != null && data != null) {
            if (result != 0) {
                int code = RD_Process(data, width, height, channel);
                this.error.mCode = FT_GetErrorCode(result);
                if (code > 0) {
                    AFT_FSDKFace face = new AFT_FSDKFace();
                    RD_Result(face);
                }
            } else {
                this.error.mCode = 5;
            }
        } else {
            this.error.mCode = 2;
        }

        return this.error;
    }

    public AFT_FSDKError AFT_FSDK_UninitialFaceEngine() {
        if (result != 0) {
            this.error.mCode = this.FT_UnInit(result);
            result = 0;
        } else {
            this.error.mCode = 5;
        }

        return this.error;
    }

    public AFT_FSDKError AFT_FSDK_GetVersion(AFT_FSDKVersion version) {
        if (version == null) {
            this.error.mCode = 2;
        } else if (result != 0) {
            this.error.mCode = 0;
            version.mVersion = this.FT_Version(result);
        } else {
            this.error.mCode = 5;
        }

        return this.error;
    }

    static {
        //System.loadLibrary("mpbase");
        //System.loadLibrary("ArcSoft_FTEngine");
    }
}