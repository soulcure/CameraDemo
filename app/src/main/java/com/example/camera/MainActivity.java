package com.example.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.camera.common.RequestFeatureStatus;
import com.example.camera.customview.FaceRectView;
import com.example.camera.previewutil.FaceCameraHelper;
import com.example.camera.previewutil.FaceTrackListener;
import com.example.camera.sdk.FaceEngine;
import com.example.camera.sdk.Face;
import com.example.image.R;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity implements FaceTrackListener {
    private static final String TAG = "MainActivity";

    private static final int ACTION_REQUEST_PERMISSIONS = 1;

    private FaceEngine ftEngine;

    private TextureView textureViewPreview;
    private FaceRectView faceRectView;
    private Camera.Size previewSize;

    private FaceCameraHelper faceCameraHelper;
    private volatile ConcurrentHashMap<Integer, RequestFeatureStatus> requestFeatureStatusMap = new ConcurrentHashMap<>();


    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textureViewPreview = findViewById(R.id.textureview_preview);
        faceRectView = findViewById(R.id.facerect_view);
        initEngine();

        if (checkPermissions()) {
            initCamera(textureViewPreview, faceRectView, ftEngine);
        }
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                initCamera(textureViewPreview, faceRectView, ftEngine);
                faceCameraHelper.start();
            } else {
                Toast.makeText(this, "权限不足", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        unInitEngine();
        super.onDestroy();
    }

    @Override
    public void onPreviewData(byte[] nv21, List<Face> ftFaceList, List<Integer> trackIdList) {
        Log.i(TAG, "onPreviewData: " + trackIdList.size());
        //请求获取人脸特征数据
        if (ftFaceList.size() > 0 && previewSize != null) {
            for (int i = 0; i < ftFaceList.size(); i++) {
                if (requestFeatureStatusMap.get(trackIdList.get(i)) == null
                        || requestFeatureStatusMap.get(trackIdList.get(i)) == RequestFeatureStatus.FAILED) {
                    faceCameraHelper.requestFaceFeature(nv21, ftFaceList.get(i).getRect(), previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, ftFaceList.get(i).getDegree(), trackIdList.get(i));
                    requestFeatureStatusMap.put(trackIdList.get(i), RequestFeatureStatus.SEARCHING);
                }
                clearLeftFace(trackIdList);
            }
        }
    }


    @Override
    public void onFail(Exception e) {
        Log.i(TAG, "onFail: " + e.getMessage());
    }

    @Override
    public void onCameraOpened(Camera camera) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        Log.i(TAG, "onCameraOpened:  previewSize is " + previewSize.width + "x" + previewSize.height);
        this.previewSize = previewSize;
    }

    @Override
    public void adjustFaceRectList(List<Face> ftFaceList, List<Integer> trackIdList) {

    }



    public void initCamera(@NonNull View previewView, @Nullable FaceRectView faceRectView, FaceEngine ftEngine) {
        faceCameraHelper = new FaceCameraHelper.Builder()
                .activity(this)
                .specificCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT)
                .faceRectColor(Color.YELLOW)    // 人脸框颜色
                .faceRectThickness(5)   //人脸框厚度
                .previewOn(previewView) //预览画面显示控件，支持SurfaceView和TextureView
                .faceTrackListener(this)    //监听回调设置
                .ftEngine(ftEngine)
                .faceRectView(faceRectView) //人脸框绘制的控件
                .currentTrackId(1)  // 设置一个初始的trackID,后续在此增加
                .build();
        faceCameraHelper.init();
    }

    private void initEngine() {
        ftEngine = new FaceEngine();
        //ftError = ftEngine.RD_InitialFaceEngine();
        //Log.i(TAG, "init ftEngine: " + ftError.getCode());
    }

    private void unInitEngine() {
        /*if (ftError.getCode() == 0) {
            ftEngine.AFT_FSDK_UninitialFaceEngine();
        }*/
    }

    /**
     * 删除已经离开的人脸
     *
     * @param trackIdList
     */
    private void clearLeftFace(List<Integer> trackIdList) {
        Set<Integer> keySet = requestFeatureStatusMap.keySet();
        for (Integer integer : keySet) {
            if (!trackIdList.contains(integer)) {
                requestFeatureStatusMap.remove(integer);
            }
        }
    }

}