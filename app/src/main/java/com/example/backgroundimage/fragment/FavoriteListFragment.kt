package com.example.backgroundimage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backgroundimage.activity.DetailsActivity
import com.example.backgroundimage.activity.PurchaseInAppActivity
import com.example.backgroundimage.adapters.FavoriteAdapter
import com.example.backgroundimage.app.MainApp
import com.example.backgroundimage.local.sqlite.FavoriteDb
import com.example.backgroundimage.models.content.Posts
import com.example.backgroundimage.models.favorite.Favorite
import com.example.backgroundimage.utils.ActivityUtils
import com.example.backgroundimage.utils.AppConstant
import com.example.backgroundimage.utils.DialogUtils
import com.thn.backgroundimage.R
import com.thn.backgroundimage.databinding.ActivityCommonRecyclerBinding

class FavoriteListFragment : Fragment() {
    private var mPostList: ArrayList<Posts>? = null
    private var mFavouriteList: ArrayList<Favorite>? = null
    private var mFavoriteAdapter: FavoriteAdapter? = null
    private var mRecycler: RecyclerView? = null
    private var mFavoriteDb: FavoriteDb? = null
    private var mAdapterPosition = 0
    private lateinit var binding: ActivityCommonRecyclerBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityCommonRecyclerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVar()
        initView()
        initListener()
    }

    private fun initVar() {
        mPostList = ArrayList()
        mFavouriteList = ArrayList()
    }

    private fun initView() {
        mFavoriteDb = FavoriteDb(requireContext())
        binding.toolbarTop.toolbarTitle.setText(R.string.site_menu_fav)
        binding.toolbarTop.pointWallet.text = MainApp.getInstance().valueCoin.toString() + ""
        mRecycler = binding.rvContent
        mRecycler!!.layoutManager =
            GridLayoutManager(requireActivity(), 3, GridLayoutManager.VERTICAL, false)
        mFavoriteAdapter = FavoriteAdapter(requireContext(), activity, mFavouriteList)
        mRecycler!!.adapter = mFavoriteAdapter
        binding.toolbarTop.imagebtn.setOnClickListener {
            val manager: FragmentManager = requireActivity().supportFragmentManager
            val dialog = DialogUtils.newInstance(
                getString(R.string.site_menu_fav),
                getString(R.string.delete_all_fav_item),
                getString(R.string.yes),
                getString(R.string.no),
                AppConstant.BUNDLE_KEY_DELETE_ALL_FAV
            )
            dialog.setOnCompleteListener { isOkPressed, viewIdText ->
                if (isOkPressed) {
                    if (viewIdText == AppConstant.BUNDLE_KEY_DELETE_ALL_FAV) {
                        mFavoriteDb!!.deleteAllFav()
                        updateUI()
                    } else if (viewIdText == AppConstant.BUNDLE_KEY_DELETE_EACH_FAV) {
                        mFavoriteDb!!.deleteEachFav(mFavouriteList!![mAdapterPosition].title)
                        updateUI()
                    }
                }
            }
            dialog.show(manager, AppConstant.BUNDLE_KEY_DIALOG_FRAGMENT)
        }
        binding.toolbarTop.pointView.setOnClickListener {
            ActivityUtils.getInstance()
                .invokeNewActivity(requireActivity(), PurchaseInAppActivity::class.java, false)
        }
    }

    fun showLoader() {
        binding.viewCommonLoader.progressBar.visibility = View.VISIBLE
        binding.viewCommonLoader.noDataView.visibility = View.GONE
    }

    fun hideLoader() {
        binding.viewCommonLoader.progressBar.visibility = View.GONE
        binding.viewCommonLoader.noDataView.visibility = View.GONE
    }

    fun showEmptyView() {
        binding.viewCommonLoader.progressBar.visibility = View.GONE
        binding.viewCommonLoader.noDataView.visibility = View.VISIBLE
    }

    fun updateUI() {
        showLoader()
        mFavouriteList!!.clear()
        mFavouriteList!!.addAll(mFavoriteDb!!.allData)
        mPostList!!.clear()
        for (i in mFavouriteList!!.indices) {
            val model = mFavouriteList!![i]
            val post = Posts(
                model.title,
                model.postCategory,
                AppConstant.EMPTY_STRING,
                model.imageUrl,
                true,
                model.isNeedPont
            )
            mPostList!!.add(post)
        }
        mFavoriteAdapter!!.notifyDataSetChanged()
        hideLoader()
        if (mFavouriteList!!.size == 0) {
            showEmptyView()
            binding.toolbarTop.imagebtn.visibility = View.INVISIBLE
        } else {
            binding.toolbarTop.imagebtn.visibility = View.VISIBLE
        }
    }

    fun initListener() {
        // recycler list item click listener
        mFavoriteAdapter!!.setItemClickListener { position, view ->
            mAdapterPosition = position
            if (view.id == R.id.btn_fav) {
                val manager: FragmentManager = childFragmentManager

                val dialog: DialogUtils = DialogUtils.newInstance(
                    getString(R.string.site_menu_fav),
                    getString(R.string.delete_all_fav_item),
                    getString(R.string.yes),
                    getString(R.string.no),
                    AppConstant.BUNDLE_KEY_DELETE_ALL_FAV
                )
                dialog.show(manager, AppConstant.BUNDLE_KEY_DIALOG_FRAGMENT)
            } else if (view.id == R.id.card_view_top) ActivityUtils.getInstance()
                .invokeDetailsActiviy(
                    activity,
                    DetailsActivity::class.java,
                    mPostList,
                    position,
                    false
                )
        }
    }



    override fun onResume() {
        super.onResume()
        if (mFavoriteAdapter != null) {
            updateUI()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            FavoriteListFragment()
    }
}