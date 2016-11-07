package com.curry.stephen.lar.logic;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.view.SurfaceView;
import android.widget.Toast;

import com.curry.stephen.lar.view.CameraPreview;

import java.util.List;

import static com.curry.stephen.lar.global.Constants.DESIRED_CAMERA_PREVIEW_WIDTH;

/**
 * Created by lingchong on 16/11/7.
 */

public class CameraManager {

    private Camera mCamera;
    private Context mContext;
    private SurfaceView mSurfaceView;

    private static final String TAG = CameraManager.class.getSimpleName();

    public CameraManager(Context context) {
        mContext = context;
    }

    public void show() {
        GetCameraAsync getCameraAsync = new GetCameraAsync();
        getCameraAsync.execute();
    }

    private Camera getCamera() {
        Camera camera = null;
        try {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return camera;
    }

    private class GetCameraAsync extends AsyncTask<Void, Void, Camera> {

        @Override
        protected Camera doInBackground(Void... voids) {
            return getCamera();
        }

        @Override
        protected void onPostExecute(Camera camera) {
            if (camera == null) {
                Toast.makeText(mContext, "打开摄像头失败.", Toast.LENGTH_SHORT).show();
            } else {
                mCamera = camera;
                initCameraParameters();
                mSurfaceView = new CameraPreview(mContext, mCamera, null);
                ((Activity)mContext).setContentView(mSurfaceView);
            }
        }
    }

    public void release() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void initCameraParameters() {
        if (mCamera == null) {
            return;
        }

        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        int currWidth = 0;
        int currHeight = 0;
        boolean foundDesiredWidth = false;
        for (Camera.Size size : sizes) {
            if (size.width == DESIRED_CAMERA_PREVIEW_WIDTH) {
                currWidth = size.width;
                currHeight = size.height;
                foundDesiredWidth = true;
            }
        }

        if (foundDesiredWidth) {
            parameters.setPreviewSize(currWidth, currHeight);
        }

        if (mContext.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            mCamera.setDisplayOrientation(90);

        } else {
            mCamera.setDisplayOrientation(0);
        }

        mCamera.setParameters(parameters);
    }
}
