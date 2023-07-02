package com.example.backgroundimage.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.backgroundimage.activity.CategoryWiseActivity
import com.example.backgroundimage.activity.DetailsActivity
import com.example.backgroundimage.activity.FeaturedActivity
import com.example.backgroundimage.activity.MainActivity
import com.example.backgroundimage.activity.PurchaseInAppActivity
import com.example.backgroundimage.activity.RecentActivity
import com.example.backgroundimage.activity.SearchActivity
import com.example.backgroundimage.adapters.DetailPagerAdapter
import com.example.backgroundimage.adapters.HomeCategoryAdapter
import com.example.backgroundimage.adapters.HomeRecentAdapter
import com.example.backgroundimage.app.MainApp
import com.example.backgroundimage.dialog.RateDialog
import com.example.backgroundimage.local.sqlite.FavoriteDb
import com.example.backgroundimage.model.Category
import com.example.backgroundimage.model.Image
import com.example.backgroundimage.model.WallResponse
import com.example.backgroundimage.models.content.Categories
import com.example.backgroundimage.models.content.Posts
import com.example.backgroundimage.models.favorite.Favorite
import com.example.backgroundimage.network.ClientAPI
import com.example.backgroundimage.network.InterfaceAPI
import com.example.backgroundimage.utils.ActivityUtils
import com.example.backgroundimage.utils.AppConstant
import com.thn.backgroundimage.R
import com.thn.backgroundimage.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Collections

class HomeFragment : Fragment() {

    private lateinit var mLytCategory: RelativeLayout
    private lateinit var mLytFeatured: RelativeLayout
    private lateinit var mLytRecent: RelativeLayout
    private lateinit var mLytMain: LinearLayout

    // Featured
    private var mFeaturedList: ArrayList<Posts?>? = null
    private lateinit var mFeaturedPager: ViewPager
    private lateinit var mDetailPagerAdapter: DetailPagerAdapter

    // Category
    private var mCategoryList: ArrayList<Categories>? = arrayListOf()
    private var mHomeCategoryAdapter: HomeCategoryAdapter? = null
    private lateinit var mCategoryRecycler: RecyclerView

    // Recent
    private var mRecentPostList: ArrayList<Posts?>? = null
    private var mHomeRecentPostList: ArrayList<Posts?>? = null
    private var mRecentPostAdapter: HomeRecentAdapter? = null
    private lateinit var mRecentRecycler: RecyclerView

    // Favourites view
    private var mFavoriteList: MutableList<Favorite>? = null
    private var mFavoriteDb: FavoriteDb? = null
    private lateinit var mViewAllFeatured: ImageView
    private lateinit var mViewAllRecent: ImageView
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RateDialog.show(requireContext(), childFragmentManager)
        initVar()
        initView()
        loadData()
        initListener()
    }

    private fun initVar() {
        mCategoryList = ArrayList()
        mFeaturedList = ArrayList()
        mRecentPostList = ArrayList()
        mHomeRecentPostList = ArrayList()
        mFavoriteList = ArrayList()
    }

    private fun initView() {
        mCategoryRecycler = binding.contentCategories.rvCategories
        mCategoryRecycler!!.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mHomeCategoryAdapter = HomeCategoryAdapter(context, requireActivity(), mCategoryList)
        mCategoryRecycler!!.adapter = mHomeCategoryAdapter
        mFeaturedPager = binding.contentFeaturedPosts.pagerFeaturedPosts
        mViewAllFeatured = binding.contentFeaturedPosts.viewAllFeatured
        mRecentRecycler = binding.contentRecentPosts.rvRecent
        mRecentRecycler!!.layoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        mRecentPostAdapter =
            HomeRecentAdapter(
                requireContext(),
                requireActivity(),
                mHomeRecentPostList
            )
        mRecentRecycler!!.adapter = mRecentPostAdapter
        mViewAllRecent = binding.contentRecentPosts.viewAllRecent
        mLytCategory = binding.contentCategories.lytCategories
        mLytFeatured = binding.contentFeaturedPosts.lytFeatured
        mLytRecent = binding.contentRecentPosts.lytRecent
        mLytMain = binding.lytMain
        binding.toolbar.pointWallet.text = MainApp.getInstance().valueCoin.toString() + ""
        binding.toolbar.pointView.setOnClickListener {
            ActivityUtils.getInstance()
                .invokeNewActivity(requireActivity(), PurchaseInAppActivity::class.java, false)
        }
        binding.toolbar.imgBtnSearch!!.setOnClickListener {
            ActivityUtils.getInstance()
                .invokeNewActivity(requireActivity(), SearchActivity::class.java, false)
        }

    }

    private fun initListener() {


        // Search button click listener


        // recycler list item click listener
        mHomeCategoryAdapter!!.setItemClickListener { position, view ->
            val model = mCategoryList!![position]
            if (view.id == R.id.lyt_container) ActivityUtils.getInstance()
                .invokeCatWisePostListActiviy(
                    requireActivity(),
                    CategoryWiseActivity::class.java,
                    model.title,
                    false
                )
        }
        mRecentPostAdapter!!.setItemClickListener { position, view ->
            val model = mRecentPostList!![position]
            if (view.id == R.id.btn_fav) {
                if (model!!.isFavorite) {

                    mFavoriteDb!!.deleteEachFav(mRecentPostList!![position]!!.title)
                    mRecentPostList!![position]!!.isFavorite = false
                    mHomeRecentPostList!![position]!!.isFavorite = false
                    mRecentPostAdapter!!.notifyDataSetChanged()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.removed_from_fav),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    mFavoriteDb!!.insertData(model.title, model.imageUrl, model.category)
                    mRecentPostList!![position]!!.isFavorite = true
                    mHomeRecentPostList!![position]!!.isFavorite = true
                    mRecentPostAdapter!!.notifyDataSetChanged()
                    Toast.makeText(
                        this.requireContext(),
                        getString(R.string.added_to_fav),
                        Toast.LENGTH_SHORT
                    ).show()

                }
                (activity as MainActivity).onReloadFov()
            } else if (view.id == R.id.card_view_top) {
                if (model!!.isNeedPoint) {
                    if (MainApp.getInstance().valueCoin >= 2) {
                        MainApp.getInstance().valueCoin = MainApp.getInstance().valueCoin - 2
                        ActivityUtils.getInstance().invokeDetailsActiviy(
                            requireActivity(),
                            DetailsActivity::class.java,
                            mHomeRecentPostList,
                            position,
                            false
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "You need more coin to using this image!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    ActivityUtils.getInstance().invokeDetailsActiviy(
                        requireActivity(),
                        DetailsActivity::class.java,
                        mHomeRecentPostList,
                        position,
                        false
                    )
                }
            }
        }
        binding.viewCommonLoader.noDataView.setOnClickListener {
            loadData()
        }

        mViewAllFeatured!!.setOnClickListener {
            ActivityUtils.getInstance()
                .invokeNewActivity(requireActivity(), FeaturedActivity::class.java, false)
        }
        mViewAllRecent!!.setOnClickListener {
            ActivityUtils.getInstance()
                .invokeNewActivity(requireActivity(), RecentActivity::class.java, false)
        }
    }

    private fun updateUI() {
        if (mFavoriteDb == null) {
            mFavoriteDb =
                FavoriteDb(context)
        }
        mFavoriteList!!.clear()
        mFavoriteList!!.addAll(mFavoriteDb!!.allData)

        // Check for favorite
        for (i in mRecentPostList!!.indices) {
            var isFavorite = false
            for (j in mFavoriteList!!.indices) {
                if (mFavoriteList!![j].title == mRecentPostList!![i]!!.title) {
                    isFavorite = true
                    break
                }
            }
            mRecentPostList!![i]!!.isFavorite = isFavorite
        }
        if (mRecentPostList!!.size == 0) {
            showEmptyView()
        } else {
            mRecentPostAdapter!!.notifyDataSetChanged()
            hideLoader()
        }

    }


    private fun loadData() {
        showLoader()
        loadCategoriesFromFirebase()
        loadPostsFromFirebase()
    }

    private fun loadCategoriesFromFirebase() {
        val apiClient = ClientAPI.getRetrofit()
        if (apiClient != null) {
            val interfaceApi = apiClient.create(InterfaceAPI::class.java)
            interfaceApi.getCategories().enqueue(object : Callback<WallResponse<Category>?> {
                override fun onResponse(
                    call: Call<WallResponse<Category>?>,
                    response: Response<WallResponse<Category>?>
                ) {
                    if (response.isSuccessful && response.code() == 200) {
                        val wallResponse = response.body()
                        if (wallResponse != null && wallResponse.success) {
                            for (i in wallResponse.data.indices) {
                                mCategoryList!!.add(wallResponse.data[i].toCategory())
                            }
                            mLytCategory!!.visibility = View.VISIBLE
                            mLytMain!!.visibility = View.VISIBLE
                            mHomeCategoryAdapter!!.notifyDataSetChanged()
                        } else {
                            showEmptyView()
                        }
                    } else {
                        showEmptyView()
                    }
                }

                override fun onFailure(call: Call<WallResponse<Category>?>, t: Throwable) {
                    showEmptyView()
                }
            })
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


    private fun loadPostsFromFirebase() {
        val apiClient = ClientAPI.getRetrofit()
        if (apiClient != null) {
            val interfaceApi = apiClient.create(InterfaceAPI::class.java)
            interfaceApi.getImages().enqueue(object : Callback<WallResponse<Image>?> {
                override fun onResponse(
                    call: Call<WallResponse<Image>?>,
                    response: Response<WallResponse<Image>?>
                ) {
                    Log.d("Thuchs", response.code().toString() + "")
                    if (response.isSuccessful && response.code() == 200) {
                        val wallResponse = response.body()
                        if (wallResponse != null) {
                            for (i in wallResponse.data.indices) {
                                mRecentPostList!!.add(wallResponse.data[i].toPost())
                            }
                            Collections.shuffle(mRecentPostList)
                            val maxLoop: Int
                            maxLoop =
                                if (AppConstant.BUNDLE_KEY_HOME_INDEX > mRecentPostList!!.size) {
                                    mRecentPostList!!.size
                                } else {
                                    AppConstant.BUNDLE_KEY_HOME_INDEX
                                }
                            for (i in 0 until maxLoop) {
                                mHomeRecentPostList!!.add(mRecentPostList!![i])
                            }
                            for (i in mRecentPostList!!.indices) {
                                if (mRecentPostList!![i]!!.isFeatured == AppConstant.JSON_KEY_YES) {
                                    mFeaturedList!!.add(mRecentPostList!![i])
                                }
                            }
                            Collections.shuffle(mFeaturedList)
                            mDetailPagerAdapter =
                                DetailPagerAdapter(
                                    requireContext(), mFeaturedList
                                )
                            mFeaturedPager!!.adapter = mDetailPagerAdapter
                            mDetailPagerAdapter!!.setItemClickListener { position, view ->
                                if (mFeaturedList!![position]!!.isNeedPoint) {
                                    if (MainApp.getInstance().valueCoin >= 2) {
                                        MainApp.getInstance().valueCoin =
                                            MainApp.getInstance().valueCoin - 2
                                        ActivityUtils.getInstance().invokeDetailsActiviy(
                                            requireActivity(),
                                            DetailsActivity::class.java,
                                            mFeaturedList,
                                            position,
                                            false
                                        )
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "You need more coin to using this image!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else ActivityUtils.getInstance().invokeDetailsActiviy(
                                    requireActivity(),
                                    DetailsActivity::class.java,
                                    mFeaturedList,
                                    position,
                                    false
                                )
                            }
                            if (mFeaturedList!!.size > 0) {
                                mLytFeatured!!.visibility = View.VISIBLE
                            }
                            updateUI()
                            if (mRecentPostList!!.size > 0) {
                                mLytRecent!!.visibility = View.VISIBLE
                            }
                        } else {
                            showEmptyView()
                        }
                    } else {
                        showEmptyView()
                    }
                }

                override fun onFailure(call: Call<WallResponse<Image>?>, t: Throwable) {
                    showEmptyView()
                }
            })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment()
    }
}