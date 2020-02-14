package com.example.camera;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.image.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CameraExample cameraExample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        findViewById(R.id.btn_open).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
    }


    private void init() {
        cameraExample = new CameraExample();
        cameraExample.onCreate(this);

    }


    private void open() {
        cameraExample.openCamera(new Handler());
    }


    private void close() {
        cameraExample.closeCamera();
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
}
