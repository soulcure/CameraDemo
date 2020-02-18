package com.example.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

//import com.sun.jna.Memory;
//import com.sun.jna.Pointer;

import com.example.image.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import vendor.harman.hardware.evsrelay.EvsRelayCamera;
import vendor.harman.hardware.evsrelay.EvsRelayCameraManager;


////
////


public class MainActivity2 extends Activity implements TextureView.SurfaceTextureListener {
    private String TAG = "MainActivity1";
    Context mContext;
    private Surface mSurface;
    private ImageReader mImageReader;
    private EvsRelayCameraManager mCameraManager;
    private EvsRelayCamera mCamera;
    private static final int PREVIEW_WIDTH = 1920;
    private static final int PREVIEW_HEIGHT = 1080;
    public static final String TIME_STAMP_NAME = "yyyyMMdd_HHmmss";
    private static final String CAMERA_ID_33 = "/dev/video33";
    private SurfaceHolder FaceImageHolder;
    private SurfaceView FaceImageView;
    private SurfaceHolder.Callback FaceImageHolderCallback;
    private Handler mHandler;

    private HandlerThread mThreadHandler;

    private Button mOpenCameraBtn;
    private TextureView mPreviewView;
    private static int mtimes = 0;

    ////cjt
    private byte[] mDataY = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT];
    private byte[] mDataUV = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT / 2];

//    private Pointer mpYdata = new Memory(PREVIEW_WIDTH*PREVIEW_HEIGHT);
//    private Pointer mpUVdata = new Memory(PREVIEW_WIDTH*PREVIEW_HEIGHT/2);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //com.roadefend.Helper.exitFreeformMode(this);

        mContext = this;
        mCameraManager = EvsRelayCameraManager.getInstance();

        mThreadHandler = new HandlerThread("CAMERA2");
        mThreadHandler.start();
        mHandler = new Handler(mThreadHandler.getLooper());
        FaceImageView = (SurfaceView) findViewById(R.id.FaceImageView);
        FaceImageHolder = FaceImageView.getHolder();

        mPreviewView = (TextureView) findViewById(R.id.textureview);
        mPreviewView.setSurfaceTextureListener(this);

        mOpenCameraBtn = findViewById(R.id.btn_open);
        mOpenCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("11111", "onClick");
                openCamera(mHandler);
            }
        });

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    openCamera(mHandler);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
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

            Log.d("1111", "onImageAvailable");
            try {
                ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
                ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
                ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();
                int ySize = yBuffer.remaining();
                int uSize = uBuffer.remaining();
                int vSize = vBuffer.remaining();
                byte[] data = new byte[ySize + uSize + vSize];
                //NV21 data
                yBuffer.get(data, 0, ySize);
                uBuffer.get(data, ySize, uSize);
                vBuffer.get(data, ySize + uSize, vSize);
                //input to SDK
            } catch (Exception e) {
                e.printStackTrace();
            }
            image.close();
        }
    };

    public void closeCamera() {
        try {

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
        Log.d("11111", "openCamera camera");
        mCamera = mCameraManager.openCamera(CAMERA_ID_33);
        if (mCamera == null) {
            Log.e(TAG, "openCamera failed");

            return;
        }
        Log.d(TAG, "openCamera camera");
        mImageReader = ImageReader.newInstance(PREVIEW_WIDTH, PREVIEW_HEIGHT,
                ImageFormat.YUV_420_888, /*maxImages*/3);
        mImageReader.setOnImageAvailableListener(
                mOnImageAvailableListener, handler);
        if (mCamera != null) {
            createSDKSession();
            createCameraPreviewSession();
            //createSDKSession();
        }
    }

    public void WriteImageData(byte[] data) throws Exception {

        File externalCacheDir = getExternalCacheDir();
        Log.d(TAG, "getCacheDir() : " + externalCacheDir.getAbsolutePath());

        File file = new File(externalCacheDir.getAbsolutePath() + "/image.dat");


        OutputStream outputStream = new FileOutputStream(file);
        if (!file.exists()) {
            file.createNewFile();
        }

        outputStream.write(data);
        outputStream.close();
    }

    //add imagereader surface to server for getting camera stream
    private void createSDKSession() {
        Log.d("11111", "createSDKSession");
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

    //add surface to server for face preview
    public void createCameraPreviewSession() {
        try {

            Log.d("11111", "createCameraPreviewSession");
            FaceImageHolder = FaceImageView.getHolder();
            Surface mSurface = FaceImageHolder.getSurface();
            Log.d(TAG, "createCameraPreviewSession surface" + mSurface);
            if (mCamera != null) {
                ArrayList<Surface> surfaces = new ArrayList<Surface>();
                surfaces.add(mSurface);
                mCamera.startSurfaceStream(surfaces);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (FaceImageHolderCallback == null) {
            FaceImageHolderCallback = new SurfaceHolder.Callback() {

                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                           int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    FaceImageHolder.removeCallback(FaceImageHolderCallback);
                }
            };
        }
        FaceImageHolder.addCallback(FaceImageHolderCallback);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}