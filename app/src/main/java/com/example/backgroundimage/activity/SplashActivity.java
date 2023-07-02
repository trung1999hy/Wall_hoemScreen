package com.example.backgroundimage.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.backgroundimage.utils.ActivityUtils;
import com.thn.backgroundimage.R;


public class SplashActivity extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;
    private RelativeLayout mRootLayout;

    // Constants
    private static final int SPLASH_DURATION = 1500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initVar();
        initView();
    }

    private void initVar() {
        mContext = getApplicationContext();
        mActivity = SplashActivity.this;
    }

    private void initView() {
        setContentView(R.layout.activity_splash);
        mRootLayout = (RelativeLayout) findViewById(R.id.splashBody);
    }

    private void initFunctionality() {
        mRootLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityUtils.getInstance().invokeNewActivity(mActivity, MainActivity.class, true);
            }
        }, SPLASH_DURATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFunctionality();
    }
}
