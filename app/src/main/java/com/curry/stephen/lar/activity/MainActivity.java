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

import com.curry.stephen.lar.logic.CameraManager;
import com.curry.stephen.lar.view.CameraPreview;
import com.curry.stephen.lar.R;

import java.util.List;

public class MainActivity extends Activity {

    CameraManager mCameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraManager = new CameraManager(this);
        mCameraManager.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraManager.release();
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
}
