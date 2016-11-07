package com.curry.stephen.lar.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.curry.stephen.lar.global.Constants;
import com.curry.stephen.lar.jme.CameraAccessApplication;
import com.curry.stephen.lar.logic.JmeCameraManager;
import com.curry.stephen.lar.util.YCbCr2RGB;
import com.curry.stephen.lar.view.CameraPreview;
import com.jme3.app.AndroidHarness;
import com.jme3.system.android.AndroidConfigChooser;
import com.jme3.texture.Image;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by lingchong on 16/11/7.
 */

public class JmeMainActivity extends AndroidHarness {

    private Camera mCamera;
    private CameraPreview mCameraPreview;

    private byte[] mPreviewBufferRGB565 = null;
    private ByteBuffer mPreviewByteBufferRGB565 = null;
    private int mActualPreviewWidth;
    private int mActualPreviewHeight;
    private boolean mIsPixelFormatConversionNeeded = true;
    private boolean mIsPreviewStopped = false;
    private Image mImageJMECameraRGB565 = null;
    private static final String TAG = JmeCameraManager.class.getSimpleName();

    private final Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (camera != null && !mIsPreviewStopped) {
                mPreviewByteBufferRGB565.clear();
                if (mIsPixelFormatConversionNeeded) {
                    YCbCr2RGB.yCbCrToRGB565(data, mActualPreviewWidth, mActualPreviewHeight, mPreviewBufferRGB565);
                    mPreviewByteBufferRGB565.put(mPreviewBufferRGB565);
                } else {
                    mPreviewByteBufferRGB565.put(data);
                }

                mImageJMECameraRGB565.setData(mPreviewByteBufferRGB565);
                if (app != null) {
                    ((com.curry.stephen.lar.jme.CameraAccessApplication) app).setTexture(mImageJMECameraRGB565);
                }
            }
        }
    };

    public JmeMainActivity() {
        // Set the application class to run.
        appClass = CameraAccessApplication.getFullName();
        // Try ConfigType.FASTEST; or ConfigType.LEGACY if you have problems.
        eglConfigType = AndroidConfigChooser.ConfigType.BEST;
        exitDialogTitle = "R U sure to exit?";
        exitDialogMessage = "Yes";
        eglConfigVerboseLogging = false;
        screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        mouseEventsInvertX = true;
        mouseEventsInvertY = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mIsPreviewStopped = false;
        mCamera = getCameraInstance();
        if (mCamera == null) {
            Toast.makeText(this, "摄像头获取失败!", Toast.LENGTH_SHORT).show();
            return;
        }
        initCameraParameters();
        preparePreviewCallbackBuffer();
        mCameraPreview = new CameraPreview(this, mCamera, mPreviewCallback);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(1, 1);
        addContentView(mCameraPreview, layoutParams);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsPreviewStopped = true;
        releaseCamera();
        ViewGroup parent = (ViewGroup) mCameraPreview.getParent();
        parent.removeView(mCameraPreview);
    }

    public Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
        return camera;
    }

    private void initCameraParameters() {
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> sizes = mCamera.getParameters().getSupportedPreviewSizes();
        int currWidth = 0;
        int currHeight = 0;
        boolean isDesiredWidthFound = false;
        for (Camera.Size size : sizes) {
            if (size.width == Constants.DESIRED_CAMERA_PREVIEW_WIDTH) {
                Log.d(TAG, String.format("Camera支持%d✖️%d图像尺寸.", size.width, size.height));
                currWidth = size.width;
                currHeight = size.height;
                isDesiredWidthFound = true;
                break;
            }
        }

        if (isDesiredWidthFound) {
            parameters.setPreviewSize(currWidth, currHeight);
        } else {
            Log.d(TAG, String.format("Camera不支持640✖️480的图像尺寸, 将使用默认图像尺寸: %d✖%d.",
                    parameters.getPreviewSize().width, parameters.getPreviewSize().height));
        }

        List<Integer> pixelFormats = parameters.getSupportedPictureFormats();
        for (Integer pixelFormat : pixelFormats) {
            if (pixelFormat == Constants.DESIRED_CAMERA_IMAGE_FORMAT) {
                Log.d(TAG, "Camera支持RGB_565格式的图像.");
                mIsPixelFormatConversionNeeded = false;
                parameters.setPictureFormat(pixelFormat);
                break;
            }
        }

        if (mIsPixelFormatConversionNeeded) {
            Log.d(TAG, String.format("Camera传入的图像不是RGB_565格式, 默认的图像格式编号为%d, 将对Camera的图像格式进行转换.",
                    parameters.getPreviewFormat()));
        }

        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            mCamera.setDisplayOrientation(90);

        } else {
            mCamera.setDisplayOrientation(0);
        }

        mCamera.setParameters(parameters);
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void preparePreviewCallbackBuffer() {
        mActualPreviewWidth = mCamera.getParameters().getPreviewSize().width;
        mActualPreviewHeight = mCamera.getParameters().getPreviewSize().height;
        int bufferSizeRGB565 = mActualPreviewWidth * mActualPreviewHeight * 2 + 4096;
        mPreviewBufferRGB565 = new byte[bufferSizeRGB565];
        mPreviewByteBufferRGB565 = ByteBuffer.allocateDirect(bufferSizeRGB565);
        mImageJMECameraRGB565 = new Image(Image.Format.RGB565, mActualPreviewWidth, mActualPreviewHeight,
                mPreviewByteBufferRGB565);
    }
}
