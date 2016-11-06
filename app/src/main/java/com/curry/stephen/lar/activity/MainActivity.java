package com.curry.stephen.lar.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.curry.stephen.lar.view.CameraPreview;
import com.curry.stephen.lar.R;

import java.util.List;

public class MainActivity extends Activity {

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private SurfaceView mSurfaceView;

    private static final int DESIRED_CAMERA_PREVIEW_WIDTH = 640;
    private static final int DESIRED_CAMERA_PREVIEW_HEIGHT = 480;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        mCamera = getCamera();
//        initCameraParameters();
//        mSurfaceView = new CameraPreview(this, mCamera);
//        setContentView(mSurfaceView);

        GetCameraAsync getCameraAsync = new GetCameraAsync();
        getCameraAsync.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Camera getCamera() {
        Camera camera = null;
        try {
            camera = Camera.open(0);
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
                Toast.makeText(MainActivity.this, "打开摄像头失败.", Toast.LENGTH_SHORT).show();
            } else {
                mCamera = camera;
                initCameraParameters();
                mSurfaceView = new CameraPreview(MainActivity.this, mCamera);
                setContentView(mSurfaceView);
            }
        }
    }

    private void releaseCamera() {
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

        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            mCamera.setDisplayOrientation(90);

        } else {
            mCamera.setDisplayOrientation(0);
        }

        mCamera.setParameters(parameters);
    }
}
