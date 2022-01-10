package com.nelu.wallpaper.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.nelu.wallpaper.R
import com.nelu.wallpaper.adapter.PagerAdapter
import com.nelu.wallpaper.databinding.ActivityMainBinding
import com.nelu.wallpaper.fragment.Home
import com.nelu.wallpaper.fragment.Saved

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBottomNavigation()

        val adView = AdView(this)
        MobileAds.initialize(this)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


    }

    private fun initBottomNavigation() {
        binding.viewPager.adapter = PagerAdapter(
            supportFragmentManager, 1
        ).apply {
            addFragment(Home())
            addFragment(Saved())
        }

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                Log.d("VIEW PAGER", "On Page Scrolled")
            }

            override fun onPageSelected(position: Int) {
                binding.bottomNav.menu.getItem(position).isChecked = true
            }

            override fun onPageScrollStateChanged(state: Int) {
                Log.d("VIEW PAGER", "On Page Scroll State Changed")
            }
        })

        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> binding.viewPager.currentItem = 0
                R.id.nav_saved -> binding.viewPager.currentItem = 1
            }
            false
        }

        binding.viewPager.offscreenPageLimit = 2
    }
}