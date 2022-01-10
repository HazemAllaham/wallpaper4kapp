package com.nelu.wallpaper.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.nelu.wallpaper.R
import com.nelu.wallpaper.`interface`.Adapter
import com.nelu.wallpaper.adapter.ImageAdapter
import com.nelu.wallpaper.databinding.FragmentSavedBinding
import com.nelu.wallpaper.model.Pexel
import com.nelu.wallpaper.sql.Bookmark

class Saved : Fragment() {

    private val data = ArrayList<Pexel>()
    private var adapter: ImageAdapter? = null
    private var binding: FragmentSavedBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentSavedBinding.inflate(inflater, container, false)

        adapter = ImageAdapter(requireContext(), data,
            object : Adapter {
                override fun last() {

                }
            }, R.layout.layout_image
        )

        binding?.savedRecycler?.layoutManager = LinearLayoutManager(requireContext())
        binding?.savedRecycler?.adapter = adapter

//        val adView = AdView(activity)
//        MobileAds.initialize(activity)
//        adView.adSize = AdSize.BANNER
//        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
//
//        mAdView = requireActivity().findViewById(R.id.adView)
//        val adRequest = AdRequest.Builder().build()
//        mAdView.loadAd(adRequest)



        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        data.clear()
        data.addAll(Bookmark(requireContext()).readData())
        adapter?.notifyDataSetChanged()
    }
}