package com.curry.stephen.lar.view;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by lingchong on 16/11/4.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Camera.PreviewCallback mPreviewCallback;
    private static final String TAG = CameraPreview.class.getSimpleName();

    public CameraPreview(Context context, Camera camera, Camera.PreviewCallback previewCallback) {
        super(context);
        mCamera = camera;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mPreviewCallback = previewCallback;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        if (mSurfaceHolder.getSurface() == null) {
            return;
        }

        if (mCamera == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        try {
            if (mPreviewCallback != null) {
                mCamera.setPreviewCallback(mPreviewCallback);
            }
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//        if (mCamera != null) {
//            mCamera.setPreviewCallback(null);
//            mCamera.stopPreview();
//            mCamera.release();
//            mCamera = null;
//        }
    }
}
