package com.nelu.wallpaper.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.nelu.wallpaper.R
import com.nelu.wallpaper.`class`.Fetch
import com.nelu.wallpaper.`interface`.Adapter
import com.nelu.wallpaper.`interface`.Image
import com.nelu.wallpaper.adapter.ImageAdapter
import com.nelu.wallpaper.databinding.ActivitySearchBinding
import com.nelu.wallpaper.model.Pexel
import eightbitlab.com.blurview.RenderScriptBlur

class SearchActivity : AppCompatActivity() {

    private var processing = false
    private var fetch: Fetch? = null
    private val imageData = ArrayList<Pexel>()
    private lateinit var adapter: ImageAdapter
    private lateinit var binding: ActivitySearchBinding
    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.back.clipToOutline = true
        binding.searchIcon.clipToOutline = true
        binding.back.setOnClickListener { finish() }
        initBlurView()


        // for the Ads
        val adView = AdView(this)
        MobileAds.initialize(this)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        // this is adapter

        adapter = ImageAdapter(this, imageData,
            object : Adapter {
                override fun last() {
                    if (!processing) {
                        processing = true
                        fetch?.getData()
                    }
                }
            }, R.layout.layout_image
        )




        binding.searchRecycler.layoutManager = LinearLayoutManager(this)
        binding.searchRecycler.adapter = adapter

        binding.search.setOnEditorActionListener(
            TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search(binding.search.text.toString())
                return@OnEditorActionListener true
            }
            false
        })

        binding.searchIcon.setOnClickListener {
            search(binding.search.text.toString())
        }
    }

    private fun initBlurView() {
        val radius = 20f
        val decorView: View = window.decorView
        val windowBackground: Drawable = decorView.background
        binding.blurView.setupWith(decorView.findViewById(android.R.id.content))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setBlurAutoUpdate(true)
            .setHasFixedTransformationMatrix(true)
        binding.blurView.clipToOutline = true
    }

    private fun search(query: String) {
        imageData.clear()
        adapter.notifyDataSetChanged()
        fetch = Fetch(this,
            object : Image {
                override fun loaded(data: ArrayList<Pexel>) {
                    val l = data.size
                    imageData.addAll(data)
                    adapter.notifyItemRangeInserted(l, data.size)
                    adapter.notifyDataSetChanged()
                    processing = false
                }
            }, "https://api.pexels.com/v1/search?query=$query&per_page=20")
            .also { it.getData() }
    }
}