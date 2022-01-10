package com.nelu.wallpaper.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.nelu.wallpaper.R
import com.nelu.wallpaper.`class`.Fetch
import com.nelu.wallpaper.`interface`.Adapter
import com.nelu.wallpaper.`interface`.Image
import com.nelu.wallpaper.adapter.ImageAdapter
import com.nelu.wallpaper.databinding.FragmentPortraitBinding
import com.nelu.wallpaper.model.Pexel

class Portrait : Fragment() {

    private var processing = false
    private var fetch: Fetch? = null
    private val imageData = ArrayList<Pexel>()
    private lateinit var adapter: ImageAdapter
    private var binding: FragmentPortraitBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentPortraitBinding.inflate(
            inflater, container, false
        )

        adapter = ImageAdapter(requireContext(), imageData,
            object : Adapter {
                override fun last() {
                    if (!processing) {
                        processing = true
                        fetch?.getData()
                    }
                }
            }, R.layout.layout_image
        )

        binding?.portraitRecycler?.layoutManager =
            LinearLayoutManager(requireContext())
        binding?.portraitRecycler?.adapter = adapter

        fetch = Fetch(requireContext(),
            object : Image {
                override fun loaded(data: ArrayList<Pexel>) {
                    processing = false
                    imageData.addAll(data)
                    adapter.notifyDataSetChanged()
                }
            }, "https://api.pexels.com/v1/search?query=portrait&per_page=20")
        fetch?.getData()

        return binding?.root
    }
}