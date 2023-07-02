package com.example.backgroundimage.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.backgroundimage.fragment.CategoryListFragment;
import com.example.backgroundimage.fragment.FavoriteListFragment;
import com.example.backgroundimage.utils.ActivityUtils;
import com.example.backgroundimage.utils.AppConstant;
import com.example.backgroundimage.utils.DialogUtils;
import com.google.android.material.navigation.NavigationView;
import com.thn.backgroundimage.R;


public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DialogUtils.OnCompleteListener {

    private Activity mActivity;
    private LinearLayout mLoadingView, mNoDataView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = BaseActivity.this;

        // uncomment this line to disable ads from entire application
        //disableAds();

    }

    public void initToolbar(boolean isTitleEnabled) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(isTitleEnabled);
    }

    public void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    public void enableUpButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }


    public void initLoader() {
        mLoadingView = (LinearLayout) findViewById(R.id.loadingView);
        mNoDataView = (LinearLayout) findViewById(R.id.noDataView);
    }


    public void showLoader() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.VISIBLE);
        }

        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.GONE);
        }
    }

    public void hideLoader() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.GONE);
        }
    }

    public void showEmptyView() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            ActivityUtils.getInstance().invokeNewActivity(mActivity, MainActivity.class, true);
        } else /*if (id == R.id.action_gif) {
            ActivityUtilities.getInstance().invokeNewActivity(mActivity, GifListActivity.class, false);
        } else*/ if (id == R.id.action_categories) {
            ActivityUtils.getInstance().invokeNewActivity(mActivity, CategoryListFragment.class, false);
        } else if (id == R.id.action_fav) {
            ActivityUtils.getInstance().invokeNewActivity(mActivity, FavoriteListFragment.class, false);
        }
        return true;

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onComplete(Boolean isOkPressed, String viewIdText) {
        if (isOkPressed) {
            if (viewIdText.equals(AppConstant.BUNDLE_KEY_EXIT_OPTION)) {
                mActivity.finishAffinity();
            }
        }
    }
}