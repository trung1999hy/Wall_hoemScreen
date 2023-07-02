package com.example.backgroundimage.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.BuildConfig;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.backgroundimage.adapters.DetailPagerAdapter;
import com.example.backgroundimage.app.MainApp;
import com.example.backgroundimage.listeners.ItemClickListener;
import com.example.backgroundimage.local.sqlite.FavoriteDb;
import com.example.backgroundimage.models.content.Posts;
import com.example.backgroundimage.models.favorite.Favorite;
import com.example.backgroundimage.utils.ActivityUtils;
import com.example.backgroundimage.utils.AppConstant;
import com.example.backgroundimage.utils.AppUtils;
import com.example.backgroundimage.utils.PermissionUtils;
import com.thn.backgroundimage.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends BaseActivity {
    private Activity mActivity;
    private Context mContext;
    private ArrayList<Posts> mItemList;
    private int mCurrentIndex;
    private ViewPager mViewPager;
    private DetailPagerAdapter mPagerAdapter = null;
    private TextView mTxtCounter;
    private ImageButton mBtnFavorite, mBtnShare, mBtnSetWall, mBtnDownloadWall;
    private Bitmap mBitmap;

    // Favourites view
    private List<Favorite> mFavoriteList;
    private FavoriteDb mFavoriteDb;
    private boolean mIsFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
        initListener();
    }

    private void initVar() {
        mActivity = DetailsActivity.this;
        mContext = mActivity.getApplicationContext();

        mFavoriteList = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null) {
            mCurrentIndex = intent.getIntExtra(AppConstant.BUNDLE_KEY_INDEX, 0);
            mItemList = intent.getParcelableArrayListExtra(AppConstant.BUNDLE_KEY_ITEM);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_details);

        mViewPager = (ViewPager) findViewById(R.id.pager);

        mTxtCounter = (TextView) findViewById(R.id.menus_counter);
        mBtnFavorite = (ImageButton) findViewById(R.id.menus_fav);
        mBtnShare = (ImageButton) findViewById(R.id.menus_share);
        mBtnSetWall = (ImageButton) findViewById(R.id.menus_set_image);
        mBtnDownloadWall = (ImageButton) findViewById(R.id.menus_download_image);

        initLoader();
        initToolbar(false);
        setToolbarTitle(getString(R.string.details_text));
        enableUpButton();
    }

    private void initFunctionality() {
        showLoader();
        mPagerAdapter = new DetailPagerAdapter(mActivity, mItemList, true);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mCurrentIndex);

        mFavoriteDb = new FavoriteDb(mContext);
        mFavoriteList.addAll(mFavoriteDb.getAllData());
        isFavorite();

        getBitmap();

        updateCounter();

        hideLoader();

    }


    public void initListener() {
        mBtnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Posts model = mItemList.get(mViewPager.getCurrentItem());
                mIsFavorite = !mIsFavorite;
                if (mIsFavorite) {
                    mFavoriteDb.insertData(model.getTitle(), model.getImageUrl(), model.getCategory());
                    mFavoriteList.add(new Favorite(AppConstant.BUNDLE_KEY_ZERO_INDEX, model.getTitle(), model.getImageUrl(), model.getCategory()));
                    Toast.makeText(getApplicationContext(), getString(R.string.added_to_fav), Toast.LENGTH_SHORT).show();
                } else {
                    mFavoriteDb.deleteEachFav(model.getTitle());
                    for (int i = 0; i < mFavoriteList.size(); i++) {
                        if (mFavoriteList.get(i).getTitle().equals(model.getTitle())) {
                            mFavoriteList.remove(i);
                            break;
                        }
                    }
                    Toast.makeText(getApplicationContext(), getString(R.string.removed_from_fav), Toast.LENGTH_SHORT).show();
                }
                setFavorite();
            }
        });

        mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionUtils.isPermissionGranted(mActivity, PermissionUtils.SD_READ_WRITE_PERMISSIONS, PermissionUtils.REQUEST_READ_WRITE_STORAGE_DOWNLOAD)) {
                    try {
                        File file = new File(mActivity.getExternalCacheDir(), Html.fromHtml(mItemList.get(mViewPager.getCurrentItem()).getTitle()).toString().trim() + ".png");
                        FileOutputStream fOut = new FileOutputStream(file);
                        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                        file.setReadable(true, false);

                        final String appPackageName = mActivity.getPackageName();
                        final Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                mActivity.getResources().getString(R.string.share_text)
                                        + " https://play.google.com/store/apps/details?id=" + appPackageName);
                        Uri uri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", file);
                        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        sendIntent.setType("image/png");
                        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mBtnSetWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemList != null) {
                    //   FragmentManager manager = getSupportFragmentManager();
                    //   DialogUtilities dialog = DialogUtilities.newInstance(getString(R.string.information), getString(R.string.information_detail), getString(R.string.yes), getString(R.string.no), AppConstant.BUNDLE_KEY_YES);
                    //   dialog.show(manager, AppConstant.BUNDLE_KEY_DIALOG_FRAGMENT);
                    ActivityUtils.getInstance().invokeWallPreviewNCropSetActiviy(mActivity, CropActivity.class, mItemList.get(mViewPager.getCurrentItem()).getImageUrl(), false);
                }
            }
        });

        mBtnDownloadWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemList != null) {
                    //  FragmentManager manager = getSupportFragmentManager();
                    //  DialogUtilities dialog = DialogUtilities.newInstance(getString(R.string.information), getString(R.string.information_detail), getString(R.string.yes), getString(R.string.no), "download");
                    //  dialog.show(manager, AppConstant.BUNDLE_KEY_DIALOG_FRAGMENT);
                    AppUtils.downloadFile(mContext, mActivity, mBitmap);
                }
            }
        });

        mPagerAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Posts model = mItemList.get(position);
                if (view.getId() == R.id.card_view_top)
                    if (mItemList != null) {
                        ActivityUtils.getInstance().invokeWallPreviewNCropSetActiviy(mActivity, PreviewActivity.class, model.getImageUrl(), false);
                    }

            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateCounter();
                mIsFavorite = false;
                isFavorite();
                getBitmap();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onComplete(Boolean isOkPressed, String viewIdText) {
        if (viewIdText.equals(AppConstant.BUNDLE_KEY_YES)) {
            if (MainApp.getInstance().getValueCoin() >= 2) {
                MainApp.getInstance().setValueCoin(MainApp.getInstance().getValueCoin() - 2);
                ActivityUtils.getInstance().invokeWallPreviewNCropSetActiviy(mActivity, CropActivity.class, mItemList.get(mViewPager.getCurrentItem()).getImageUrl(), false);
            } else {
                Toast.makeText(this, "You need point to use this image!", Toast.LENGTH_LONG).show();
            }
        }

        if (viewIdText.equals("download")) {
            if (MainApp.getInstance().getValueCoin() >= 2) {
                MainApp.getInstance().setValueCoin(MainApp.getInstance().getValueCoin() - 2);
                AppUtils.downloadFile(mContext, mActivity, mBitmap);
            } else {
                Toast.makeText(this, "You need point to use this image!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void isFavorite() {
        for (int i = 0; i < mFavoriteList.size(); i++) {
            if (mFavoriteList.get(i).getTitle().equals(mItemList.get(mViewPager.getCurrentItem()).getTitle())) {
                mIsFavorite = true;
                break;
            }
        }
        setFavorite();
    }

    public void setFavorite() {
        if (mIsFavorite) {
            mBtnFavorite.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.ic_fav));
        } else {
            mBtnFavorite.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.ic_un_fav));
        }
    }

    public void updateCounter() {
        String counter = String.format(getString(R.string.item_counter), mViewPager.getCurrentItem() + AppConstant.BUNDLE_KEY_FIRST_INDEX, mItemList.size());
        mTxtCounter.setText(counter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void getBitmap() {
        Glide.with(mContext)
                .asBitmap()
                .load(mItemList.get(mViewPager.getCurrentItem()).getImageUrl())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        mBitmap = resource;
                    }
                });
    }
}
