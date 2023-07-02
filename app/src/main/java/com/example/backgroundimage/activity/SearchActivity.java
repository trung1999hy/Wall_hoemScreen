package com.example.backgroundimage.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
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
import com.google.firebase.FirebaseApp;
import com.thn.backgroundimage.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;

    private ArrayList<Posts> mContentList;
    private ArrayList<Posts> mSearchList;
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
        mActivity = SearchActivity.this;
        mContext = mActivity.getApplicationContext();

        mContentList = new ArrayList<>();
        mFavoriteList = new ArrayList<>();
        mSearchList = new ArrayList<>();
    }

    @SuppressLint("MissingInflatedId")
    private void initView() {
        setContentView(R.layout.sreach_view);
        mRecycler = (RecyclerView) findViewById(R.id.rvContent_Search);
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new SearchAdapter(mContext, mActivity, mSearchList);
        mRecycler.setAdapter(mAdapter);

        initLoader();
        initToolbar(true);
        enableUpButton();
        setToolbarTitle(getString(R.string.search));
    }

    private void initFunctionality() {
        loadData();
    }

    public void initListener() {
        // recycler list item click listener
        mAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Posts model = mSearchList.get(position);

                if (view.getId() == R.id.btn_fav)
                    if (model.isFavorite()) {
                        mFavoriteDb.deleteEachFav(mSearchList.get(position).getTitle());
                        mContentList.get(position).setFavorite(false);
                        mSearchList.get(position).setFavorite(false);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), getString(R.string.removed_from_fav), Toast.LENGTH_SHORT).show();

                    } else {
                        mFavoriteDb.insertData(model.getTitle(), model.getImageUrl(), model.getCategory());
                        mContentList.get(position).setFavorite(true);
                        mSearchList.get(position).setFavorite(true);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), getString(R.string.added_to_fav), Toast.LENGTH_SHORT).show();

                    }
                else if (view.getId() == R.id.card_view_top)
                    ActivityUtils.getInstance().invokeDetailsActiviy(mActivity, DetailsActivity.class, mSearchList, position, false);

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
        for (int i = 0; i < mSearchList.size(); i++) {
            boolean isFavorite = false;
            for (int j = 0; j < mFavoriteList.size(); j++) {
                if (mFavoriteList.get(j).getTitle().equals(mSearchList.get(i).getTitle())) {
                    isFavorite = true;
                    break;
                }
            }
            mSearchList.get(i).setFavorite(isFavorite);
        }
        mAdapter.notifyDataSetChanged();
        if (!mSearchList.isEmpty() && mSearchList.size() > 0) {
            hideLoader();
        } else {
            showEmptyView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(getString(R.string.search));
        searchView.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchView.setIconifiedByDefault(true);
                searchView.setFocusable(true);
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();
            }
        }, 200);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //some texts here
                showLoader();
                mSearchList.clear();

                for (int i = 0; i < mContentList.size(); i++) {
                    if (mContentList.get(i).getTitle().toLowerCase().contains(newText)) {
                        mSearchList.add(mContentList.get(i));
                    }
                }

                updateUI();

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mContentList.size() != 0) {
            updateUI();
        }
    }
}
