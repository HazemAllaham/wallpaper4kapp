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
import com.nelu.wallpaper.databinding.FragmentAbstractBinding
import com.nelu.wallpaper.databinding.FragmentInspirationBinding
import com.nelu.wallpaper.model.Pexel

class Inspiration : Fragment() {

    private var processing = false
    private var fetch: Fetch? = null
    private val imageData = ArrayList<Pexel>()
    private lateinit var adapter: ImageAdapter
    private var binding: FragmentInspirationBinding? =  null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentInspirationBinding.inflate(inflater, container, false)

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

        binding?.inspirationRecycler?.layoutManager =
            LinearLayoutManager(requireContext())
        binding?.inspirationRecycler?.adapter = adapter

        fetch = Fetch(requireContext(),
            object : Image {
                override fun loaded(data: ArrayList<Pexel>) {
                    processing = false
                    imageData.addAll(data)
                    adapter.notifyDataSetChanged()
                }
            }, "https://api.pexels.com/v1/search?query=inspiration&per_page=20")
        fetch?.getData()

        return binding?.root
    }
}