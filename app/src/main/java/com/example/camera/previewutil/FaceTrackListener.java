package com.example.camera.previewutil;


import android.hardware.Camera;


import androidx.annotation.Nullable;

import com.example.camera.sdk.Face;

import java.util.List;

public interface FaceTrackListener {

    /**
     * 回传相机预览数据和人脸框位置
     *
     * @param nv21        相机预览数据
     * @param ftFaceList  待处理的人脸列表
     * @param trackIdList 人脸追踪ID列表
     */
    void onPreviewData(byte[] nv21, List<Face> ftFaceList, List<Integer> trackIdList);


    /**
     * 当出现异常时执行
     *
     * @param e 异常信息
     */
    void onFail(Exception e);


    /**
     * 当相机打开时执行
     *
     * @param camera 相机实例
     */
    void onCameraOpened(Camera camera);

    /**
     * 根据自己的需要可以删除部分人脸，比如指定区域、留下最大人脸等
     *
     * @param ftFaceList  人脸列表
     * @param trackIdList 人脸追踪ID列表
     */
    void adjustFaceRectList(List<Face> ftFaceList, List<Integer> trackIdList);

}