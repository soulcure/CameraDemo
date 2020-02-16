package com.example.camera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.example.image.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Camera mCamera;
    private CameraPreview mPreview;

    private FrameLayout preview;
    CameraExample cameraExample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preview = (FrameLayout) findViewById(R.id.camera_preview);

        init();

        findViewById(R.id.btn_open).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
    }


    private void init() {
        cameraExample = new CameraExample();
        cameraExample.onCreate(this);

    }


    private void open() {
        //cameraExample.openCamera(new Handler());

        if (checkCameraHardware(this)) {
            try {
                mCamera = Camera.open();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mPreview = new CameraPreview(this, mCamera);
            preview.addView(mPreview);
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] bytes, Camera camera) {
                    Log.e("Camera", "onPreviewFrame():" + bytes.length);
                }
            });
        }
    }


    private void close() {
        //cameraExample.closeCamera();
        mCamera.stopPreview();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_open) {
            open();
        } else if (id == R.id.btn_close) {
            close();
        } else {

        }

    }


    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }


}
