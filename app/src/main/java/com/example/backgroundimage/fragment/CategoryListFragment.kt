package com.example.backgroundimage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backgroundimage.activity.CategoryWiseActivity
import com.example.backgroundimage.activity.PurchaseInAppActivity
import com.example.backgroundimage.adapters.CategoryAdapter
import com.example.backgroundimage.app.MainApp
import com.example.backgroundimage.model.Category
import com.example.backgroundimage.model.WallResponse
import com.example.backgroundimage.models.content.Categories
import com.example.backgroundimage.network.ClientAPI.Companion.getRetrofit
import com.example.backgroundimage.network.InterfaceAPI
import com.example.backgroundimage.utils.ActivityUtils
import com.thn.backgroundimage.R
import com.thn.backgroundimage.databinding.ActivityCommonRecyclerBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryListFragment : Fragment() {
    private var mCategoryList: ArrayList<Categories>? = null
    private var mAdapter: CategoryAdapter? = null
    private var mRecycler: RecyclerView? = null
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
        mCategoryList = ArrayList()
    }

    private fun initView() {
        binding.toolbarTop.imagebtn.visibility = View.INVISIBLE
        binding.toolbarTop.toolbarTitle.setText(R.string.featured)

        binding.toolbarTop.pointWallet.text = MainApp.getInstance().valueCoin.toString() + ""
        binding.toolbarTop.pointView.setOnClickListener {
            ActivityUtils.getInstance()
                .invokeNewActivity(requireActivity(), PurchaseInAppActivity::class.java, false)
        }
        mRecycler = binding.rvContent
        mRecycler!!.layoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        mAdapter = CategoryAdapter(requireContext(), requireActivity(), mCategoryList)
        mRecycler!!.adapter = mAdapter
        loadData()
    }

    fun initListener() {
        // recycler list item click listener
        mAdapter!!.setItemClickListener { position, view ->
            val model = mCategoryList!![position]
            if (view.id == R.id.lyt_container) {
                ActivityUtils.getInstance().invokeCatWisePostListActiviy(
                    requireActivity(),
                    CategoryWiseActivity::class.java,
                    model.title,
                    false
                )
            }
        }
        binding.viewCommonLoader.noDataView.setOnClickListener {
            loadData()
        }
    }

    private fun loadData() {
        showLoader()
        loadCategoriesFromFirebase()
    }


    private fun loadCategoriesFromFirebase() {
        val apiClient = getRetrofit()
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
                            hideLoader()
                            mAdapter!!.notifyDataSetChanged()
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

    override fun onResume() {
        super.onResume()
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

    companion object {
        @JvmStatic
        fun newInstance() =
            CategoryListFragment()
    }
}