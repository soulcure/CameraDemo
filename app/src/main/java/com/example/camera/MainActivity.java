package com.example.camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.example.image.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static final String[] CAMERA_PERMISSION =
            {Manifest.permission.CAMERA};

    private static final int RC_CAMERA_PERM = 123;


    private Context mContext;
    private Camera mCamera;
    private CameraPreview mPreview;

    private FrameLayout preview;
    CameraExample cameraExample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
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
            if (hasCameraPermission()) {
                open();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, RC_CAMERA_PERM);
            }


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


    private boolean hasCameraPermission() {
        //使用兼容库就无需判断系统版本
        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.CAMERA);
        if (hasWriteStoragePermission == PackageManager.PERMISSION_GRANTED) {
            //拥有权限，执行操作
            return true;
        } else {
            //没有权限，向用户请求权限
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //通过requestCode来识别是否同一个请求
        if (requestCode == RC_CAMERA_PERM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户同意，执行操作
                open();
            } else {
                //用户不同意，向用户展示该权限作用
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.rationale_camera)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{Manifest.permission.CAMERA},
                                            RC_CAMERA_PERM);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                }
            }
        }
    }

}
