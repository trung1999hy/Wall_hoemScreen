package com.example.backgroundimage.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.backgroundimage.adapters.HomeRecentAdapter;
import com.example.backgroundimage.app.MainApp;
import com.example.backgroundimage.listeners.ItemClickListener;
import com.example.backgroundimage.local.sqlite.FavoriteDb;
import com.example.backgroundimage.model.Image;
import com.example.backgroundimage.model.WallResponse;
import com.example.backgroundimage.models.content.Posts;
import com.example.backgroundimage.models.favorite.Favorite;
import com.example.backgroundimage.network.ClientAPI;
import com.example.backgroundimage.network.InterfaceAPI;
import com.example.backgroundimage.utils.ActivityUtils;
import com.google.firebase.FirebaseApp;
import com.thn.backgroundimage.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RecentActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;

    private ArrayList<Posts> mContentList;
    private HomeRecentAdapter mAdapter = null;
    private RecyclerView mRecycler;

    // Favourites view
    private List<Favorite> mFavoriteList;
    private FavoriteDb mFavoriteDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFirebase();

        initVar();
        initView();
        initFunctionality();
        initListener();

    }

    private void initVar() {
        mActivity = RecentActivity.this;
        mContext = mActivity.getApplicationContext();

        mContentList = new ArrayList<>();
        mFavoriteList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.sreach_view);

        mRecycler = (RecyclerView) findViewById(R.id.rvContent_Search);
        mRecycler.setLayoutManager(new GridLayoutManager(mActivity, 2, GridLayoutManager.VERTICAL, false));
        mAdapter = new HomeRecentAdapter(mContext, mActivity, mContentList);
        mRecycler.setAdapter(mAdapter);

        initLoader();
        initToolbar(true);
        enableUpButton();
        setToolbarTitle(getString(R.string.recent));
    }

    private void initFunctionality() {
        loadData();

    }

    public void initListener() {
        // recycler list item click listener
        mAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Posts model = mContentList.get(position);
                if (view.getId() == R.id.btn_fav)
                    if (model.isFavorite()) {
                        mFavoriteDb.deleteEachFav(mContentList.get(position).getTitle());
                        mContentList.get(position).setFavorite(false);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), getString(R.string.removed_from_fav), Toast.LENGTH_SHORT).show();

                    } else {
                        mFavoriteDb.insertData(model.getTitle(), model.getImageUrl(), model.getCategory());
                        mContentList.get(position).setFavorite(true);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), getString(R.string.added_to_fav), Toast.LENGTH_SHORT).show();

                    }

                else if (view.getId() == R.id.card_view_top)
                    if (model.getIsNeedPoint()) {
                        if (MainApp.getInstance().getValueCoin() >= 2 && model.getIsNeedPoint()) {
                            MainApp.getInstance().setValueCoin(MainApp.getInstance().getValueCoin() - 2);
                            ActivityUtils.getInstance().invokeDetailsActiviy(mActivity, DetailsActivity.class, mContentList, position, false);
                        } else {
                            Toast.makeText(RecentActivity.this, "You need more coin to using this image!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        ActivityUtils.getInstance().invokeDetailsActiviy(mActivity, DetailsActivity.class, mContentList, position, false);

                    }

            }

        });
    }


    private void loadData() {
        showLoader();
        loadPostsFromFirebase();
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
    }

    private void loadPostsFromFirebase() {
        Retrofit apiClient = ClientAPI.Companion.getRetrofit();
        if (apiClient != null) {
            InterfaceAPI interfaceApi = apiClient.create(InterfaceAPI.class);
            interfaceApi.getImages().enqueue(new Callback<WallResponse<Image>>() {
                @Override
                public void onResponse(Call<WallResponse<Image>> call, Response<WallResponse<Image>> response) {
                    Log.d("Thuchs", response.code() + "");
                    if (response.isSuccessful() && response.code() == 200) {
                        WallResponse<Image> wallResponse = response.body();
                        if (wallResponse != null) {
                            for (int i = 0; i < wallResponse.getData().size(); i++) {
                                mContentList.add(wallResponse.getData().get(i).toPost());
                            }

                            updateUI();
                        } else {
                            showEmptyView();
                        }
                    } else {
                        showEmptyView();
                    }
                }

                @Override
                public void onFailure(Call<WallResponse<Image>> call, Throwable t) {
                    Log.d("Thuchs", t.toString());
                    showEmptyView();
                }
            });
        }
    }

    private void updateUI() {
        if (mFavoriteDb == null) {
            mFavoriteDb = new FavoriteDb(mContext);
        }
        mFavoriteList.clear();
        mFavoriteList.addAll(mFavoriteDb.getAllData());

        // Check for favorite
        for (int i = 0; i < mContentList.size(); i++) {
            boolean isFavorite = false;
            for (int j = 0; j < mFavoriteList.size(); j++) {
                if (mFavoriteList.get(j).getTitle().equals(mContentList.get(i).getTitle())) {
                    isFavorite = true;
                    break;
                }
            }
            mContentList.get(i).setFavorite(isFavorite);
        }
        mAdapter.notifyDataSetChanged();
        if (!mContentList.isEmpty() && mContentList.size() > 0) {
            hideLoader();
        } else {
            showEmptyView();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mContentList.size() != 0) {
            updateUI();
        }
    }
}
