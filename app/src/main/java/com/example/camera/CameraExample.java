package com.example.camera;

import android.content.Context;
import android.os.Handler;
import android.media.Image;
import android.media.ImageReader;
import android.view.Surface;
import android.util.Log;
import android.graphics.ImageFormat;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import vendor.harman.hardware.evsrelay.EvsRelayCamera;
import vendor.harman.hardware.evsrelay.EvsRelayCameraManager;

public class CameraExample {

    private String TAG = "CameraExample";
    Context mContext;
    private Surface mSurface;
    private ImageReader mImageReader;
    private EvsRelayCameraManager mCameraManager;
    private EvsRelayCamera mCamera;
    private static final int PREVIEW_WIDTH = 1920;
    private static final int PREVIEW_HEIGHT = 1080;
    public static final String TIME_STAMP_NAME = "yyyyMMdd_HHmmss";
    private static final String CAMERA_ID_33 = "/dev/video33";

    private boolean flag = false;

    public void onCreate(Context context) {
        mContext = context;
        mCameraManager = EvsRelayCameraManager.getInstance();
    }

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireNextImage();
            if (null == image) {
                Log.e(TAG, "Null image returned");
                return;
            }
            try {
                ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
                ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
                ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();
                int ySize = yBuffer.remaining();
                int uSize = uBuffer.remaining();
                int vSize = vBuffer.remaining();
				byte[] data = new byte[ySize + uSize + vSize];
				yBuffer.get(data, 0, ySize);
                vBuffer.get(data, ySize, vSize);
                uBuffer.get(data, ySize + vSize, uSize);

                SuExec su = new SuExec();
                int res = su.imageArray(data);
                
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            image.close();
        }
    };

    public void closeCamera() {
        try {
            flag = false;
            if (mCamera != null) {
                ArrayList<Surface> surfaces = new ArrayList<Surface>();
                if (null != mImageReader) {
                    // Close all possible pending images first.
                    Image image = mImageReader.acquireLatestImage();
                    if (image != null) {
                        image.close();
                    }
                    surfaces.add(mImageReader.getSurface());
                    mCamera.stopSurfaceStream(surfaces);
                    mCameraManager.closeCamera(mCamera);
                    Log.v(TAG, "mCamera remove mImageReader surfaces");
                }
            } else {
                Log.e(TAG, "mCamera close error");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mImageReader.close();
            mImageReader = null;
            mCamera = null;
        }
    }

    public void openCamera(Handler handler) {
        mCamera = mCameraManager.openCamera(CAMERA_ID_33);
        if (mCamera == null) {
            Log.e(TAG, "openCamera failed");
            flag = false;
            return;
        }
        Log.d(TAG, "openCamera camera");
        mImageReader = ImageReader.newInstance(PREVIEW_WIDTH, PREVIEW_HEIGHT,
               ImageFormat.YUV_420_888, /*maxImages*/3);
        mImageReader.setOnImageAvailableListener(
            mOnImageAvailableListener, handler);
        if (mCamera != null) {
            createSDKSession();
        }
    }

    private void createSDKSession() {
        try {
            if (mCamera != null) {
                ArrayList<Surface> surfaces = new ArrayList<Surface>();
                surfaces.add(mImageReader.getSurface());
                mCamera.startSurfaceStream(surfaces);
                Log.d(TAG, "createSDKSession surface");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopCameraPreviewSession() {
        try {
            if (mCamera != null) {
                ArrayList<Surface> surfaces = new ArrayList<Surface>();
                if (mSurface != null) {
                surfaces.add(mSurface);
                    mCamera.stopSurfaceStream(surfaces);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
