package com.example.backgroundimage.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.backgroundimage.listeners.ItemClickListener;
import com.example.backgroundimage.models.content.Categories;
import com.thn.backgroundimage.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context mContext;
    private Activity mActivity;

    private ArrayList<Categories> mCategoryList;
    private ItemClickListener mItemClickListener;

    public CategoryAdapter(Context mContext, Activity mActivity, ArrayList<Categories> mCategoryList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mCategoryList = mCategoryList;
    }

    public void setItemClickListener(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_recycler, parent, false);
        return new ViewHolder(view, viewType, mItemClickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView categoryImg;
        private TextView categoryTitle;
        private RelativeLayout lytContainer;
        private ItemClickListener itemClickListener;

        private RelativeLayout pointLayout;

        public ViewHolder(View itemView, int viewType, ItemClickListener itemClickListener) {
            super(itemView);

            this.itemClickListener = itemClickListener;
            // Find all views ids
            categoryImg = (ImageView) itemView.findViewById(R.id.category_img);
            categoryTitle = (TextView) itemView.findViewById(R.id.category_name);
            lytContainer = (RelativeLayout) itemView.findViewById(R.id.lyt_container);
            pointLayout = (RelativeLayout) itemView.findViewById(R.id.point_layout);

            lytContainer.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(getLayoutPosition(), view);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != mCategoryList ? mCategoryList.size() : 0);

    }

    @Override
    public void onBindViewHolder(ViewHolder mainHolder, int position) {
        final Categories model = mCategoryList.get(position);

        // setting data over views
        String imgUrl = model.getImageUrl();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .into(mainHolder.categoryImg);
        }
        mainHolder.categoryTitle.setText(Html.fromHtml(model.getTitle()));
    }
}