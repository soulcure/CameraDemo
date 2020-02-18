package com.example.camera.sdk;


import java.util.List;

public class FaceEngine {
    private final String TAG = FaceEngine.class.getSimpleName();

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

    static {
        //System.loadLibrary("mpbase");
        //System.loadLibrary("ArcSoft_FTEngine");
        System.loadLibrary("native-lib");
    }

    private int result = 0;
    private Face mFace = new Face();

    private native int RD_Init(byte[] path);

    private native int RD_Process(byte[] data, int width, int height, int channel);

    private native int RD_Result(Face face);

    private native int RD_UnInit(int var1);

    public FaceEngine() {
    }

    private Face getFace() {
        return mFace;
    }

    public void RD_InitialFaceEngine() {
        //todo
        RD_Init("test path".getBytes());
    }


    //人脸特征数据
    public int faceFeatureDetect(byte[] data, int width, int height, int channel, List<Face> list) {
        int code = -1;
        if (list != null && data != null) {
            if (result != 0) {
                code = RD_Process(data, width, height, channel);
                if (code > 0) {
                    Face face = new Face();
                    RD_Result(face);
                }
            }
        }
        return code;
    }

    public void uninitialFaceEngine() {
        RD_UnInit(result);
    }


}