package com.nelu.wallpaper.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.nelu.wallpaper.R
import com.nelu.wallpaper.`class`.Collection
import com.nelu.wallpaper.`interface`.Adapter
import com.nelu.wallpaper.`interface`.Image
import com.nelu.wallpaper.adapter.ImageAdapter
import com.nelu.wallpaper.databinding.FragmentCollectionBinding
import com.nelu.wallpaper.model.Pexel

class Collection(val id: String) : Fragment() {

    private var processing = false
    private var collection: Collection? = null
    private val imageData = ArrayList<Pexel>()
    private lateinit var adapter: ImageAdapter
    private var binding: FragmentCollectionBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentCollectionBinding.inflate(inflater, container, false)

        adapter = ImageAdapter(requireContext(), imageData,
            object : Adapter {
                override fun last() {
//                    if (!processing) {
//                        processing = true
//                        fetch?.getData()
//                        Log.e("Knock last","True")
//                    }
                }
            }, R.layout.layout_image
        )

        binding?.collectionRecycler?.layoutManager =
                StaggeredGridLayoutManager(
                    2, StaggeredGridLayoutManager.VERTICAL
                )
//        binding?.collectionRecycler?.layoutManager =
//            LinearLayoutManager(requireContext())
        binding?.collectionRecycler?.adapter = adapter


        collection = Collection(requireContext(),
            object : Image {
                override fun loaded(data: ArrayList<Pexel>) {
                    val l = data.size
                    imageData.addAll(data)
                    adapter.notifyItemRangeInserted(l, data.size)
                    adapter.notifyDataSetChanged()
                    processing = false
                    Log.e("Loaded","True")
                }
            }, "https://api.pexels.com/v1/collections/$id?per_page=20")
            .also { it.fetch() }

        return binding?.root
    }

    override fun onDetach() {
        super.onDetach()
        binding = null
    }
}