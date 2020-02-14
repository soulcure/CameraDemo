package com.example.camera;

public class SuExec {
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * 传递数据给c++
     */
    public native int imageArray(byte[] buf);
}