package com.example.backgroundimage.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.backgroundimage.adapters.MainViewPager
import com.example.backgroundimage.fragment.CategoryListFragment
import com.example.backgroundimage.fragment.FavoriteListFragment
import com.example.backgroundimage.fragment.HomeFragment
import com.example.backgroundimage.utils.AppUtils
import com.thn.backgroundimage.R
import com.thn.backgroundimage.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val favoriteListFragment = FavoriteListFragment.newInstance()
    private var listFragment: ArrayList<Fragment> = arrayListOf(
        HomeFragment.newInstance(),
        CategoryListFragment.newInstance(),
        favoriteListFragment
    )
    private var mainViewPager: MainViewPager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)
        mainViewPager = MainViewPager(supportFragmentManager)
        mainViewPager?.setData(listFragment)
        binding.fragmentContainerViewTag.adapter = mainViewPager
        binding.fragmentContainerViewTag.offscreenPageLimit = 3
        binding.fragmentContainerViewTag.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                binding.bottomNavigation.selectedItemId = when (position) {
                    0 -> R.id.action_home
                    1 -> R.id.action_categories
                    2 -> R.id.action_fav
                    else -> R.id.action_home
                }

            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
        binding.bottomNavigation.setOnItemSelectedListener() {
            binding.fragmentContainerViewTag.currentItem = when (it.itemId) {
                R.id.action_home -> 0
                R.id.action_categories -> 1
                R.id.action_fav -> 2
                else -> 0
            }
            true
        }

    }


    override fun onBackPressed() {
        AppUtils.tapPromptToExit(this)
    }

    fun onReloadFov() {
        favoriteListFragment.updateUI()
    }
}
