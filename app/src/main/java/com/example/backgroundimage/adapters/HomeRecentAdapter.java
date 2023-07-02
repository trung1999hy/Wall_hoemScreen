package com.example.backgroundimage.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.backgroundimage.listeners.ItemClickListener;
import com.example.backgroundimage.models.content.Posts;
import com.thn.backgroundimage.R;

import java.util.ArrayList;

;

public class HomeRecentAdapter extends RecyclerView.Adapter<HomeRecentAdapter.ViewHolder> {

    private Context mContext;
    private Activity mActivity;

    private ArrayList<Posts> mContentList;
    private ItemClickListener mItemClickListener;

    public HomeRecentAdapter(Context mContext, Activity mActivity, ArrayList<Posts> mContentList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mContentList = mContentList;
    }

    public void setItemClickListener(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_recycler, parent, false);
        return new ViewHolder(view, viewType, mItemClickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cardView;
        private ImageView imgPost;
        private TextView tvTitle;
        private ImageButton btnFav;
        private ItemClickListener itemClickListener;

        private RelativeLayout pointLayout;


        public ViewHolder(View itemView, int viewType, ItemClickListener itemClickListener) {
            super(itemView);

            this.itemClickListener = itemClickListener;
            // Find all views ids
            cardView = (CardView) itemView.findViewById(R.id.card_view_top);
            imgPost = (ImageView) itemView.findViewById(R.id.post_img);
            tvTitle = (TextView) itemView.findViewById(R.id.title_text);
            btnFav = (ImageButton) itemView.findViewById(R.id.btn_fav);
            pointLayout = (RelativeLayout) itemView.findViewById(R.id.point_layout);

            btnFav.setOnClickListener(this);
            cardView.setOnClickListener(this);
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
        return (null != mContentList ? mContentList.size() : 0);

    }

    @Override
    public void onBindViewHolder(ViewHolder mainHolder, int position) {
        final Posts model = mContentList.get(position);

        // setting data over views
        String imgUrl = model.getImageUrl();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .into(mainHolder.imgPost);
        }

        if (model.isFavorite()) {
            mainHolder.btnFav.setImageResource(R.drawable.ic_fav);
        } else {
            mainHolder.btnFav.setImageResource(R.drawable.ic_un_fav);
        }

        if (model.getIsNeedPoint()) {
            mainHolder.pointLayout.setVisibility(View.VISIBLE);
        } else {
            mainHolder.pointLayout.setVisibility(View.GONE);
        }
        mainHolder.tvTitle.setText(Html.fromHtml(model.getTitle()));
    }
}