package com.example.backgroundimage.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.backgroundimage.adapters.SearchAdapter;
import com.example.backgroundimage.listeners.ItemClickListener;
import com.example.backgroundimage.local.sqlite.FavoriteDb;
import com.example.backgroundimage.model.Image;
import com.example.backgroundimage.model.WallResponse;
import com.example.backgroundimage.models.content.Posts;
import com.example.backgroundimage.models.favorite.Favorite;
import com.example.backgroundimage.network.ClientAPI;
import com.example.backgroundimage.network.InterfaceAPI;
import com.example.backgroundimage.utils.ActivityUtils;
import com.example.backgroundimage.utils.AppConstant;
import com.google.firebase.FirebaseApp;
import com.thn.backgroundimage.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FeaturedActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;

    private ArrayList<Posts> mContentList;
    private ArrayList<Posts> mFeaturedList;
    private SearchAdapter mAdapter = null;
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
        mActivity = FeaturedActivity.this;
        mContext = mActivity.getApplicationContext();

        mContentList = new ArrayList<>();
        mFavoriteList = new ArrayList<>();
        mFeaturedList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.sreach_view);

        mRecycler = (RecyclerView) findViewById(R.id.rvContent_Search);
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new SearchAdapter(mContext, mActivity, mFeaturedList);
        mRecycler.setAdapter(mAdapter);

        initLoader();
        initToolbar(true);
        enableUpButton();
        setToolbarTitle(getString(R.string.featured));
    }

    private void initFunctionality() {
        loadData();
    }

    public void initListener() {
        // recycler list item click listener
        mAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Posts model = mFeaturedList.get(position);

                if (view.getId() == R.id.btn_fav) {
                    if (model.isFavorite()) {
                        mFavoriteDb.deleteEachFav(mFeaturedList.get(position).getTitle());
                        mContentList.get(position).setFavorite(false);
                        mFeaturedList.get(position).setFavorite(false);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), getString(R.string.removed_from_fav), Toast.LENGTH_SHORT).show();
                    } else {
                        mFavoriteDb.insertData(model.getTitle(), model.getImageUrl(), model.getCategory());
                        mContentList.get(position).setFavorite(true);
                        mFeaturedList.get(position).setFavorite(true);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), getString(R.string.added_to_fav), Toast.LENGTH_SHORT).show();
                    }
                } else if (view.getId() == R.id.card_view_top)
                    ActivityUtils.getInstance().invokeDetailsActiviy(mActivity, DetailsActivity.class, mFeaturedList, position, false);

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

                            for (int i = 0; i < mContentList.size(); i++) {
                                if (mContentList.get(i).getIsFeatured().equals(AppConstant.JSON_KEY_YES)) {
                                    mFeaturedList.add(mContentList.get(i));
                                }
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
        for (int i = 0; i < mFeaturedList.size(); i++) {
            boolean isFavorite = false;
            for (int j = 0; j < mFavoriteList.size(); j++) {
                if (mFavoriteList.get(j).getTitle().equals(mFeaturedList.get(i).getTitle())) {
                    isFavorite = true;
                    break;
                }
            }
            mFeaturedList.get(i).setFavorite(isFavorite);
        }
        mAdapter.notifyDataSetChanged();
        if (!mFeaturedList.isEmpty() && mFeaturedList.size() > 0) {
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
