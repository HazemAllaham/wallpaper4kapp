package com.nelu.wallpaper.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nelu.wallpaper.R
import com.nelu.wallpaper.`class`.Fetch
import com.nelu.wallpaper.`interface`.Adapter
import com.nelu.wallpaper.`interface`.Image
import com.nelu.wallpaper.adapter.ImageAdapter
import com.nelu.wallpaper.databinding.ActivityColorBinding
import com.nelu.wallpaper.model.Pexel

class ColorActivity : AppCompatActivity() {

    companion object {
        fun launch(context: Context, color: String) {
            context.startActivity(
                Intent(
                    context,
                    ColorActivity::class.java
                ).putExtra("COLOR", color)
            )
        }
    }

    private var processing = false
    private var fetch: Fetch? = null
    private val imageData = ArrayList<Pexel>()
    private var adapter: ImageAdapter? = null
    private var binding: ActivityColorBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityColorBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.back?.setOnClickListener { finish() }

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
        binding?.colorRecycler?.layoutManager = LinearLayoutManager(this)
        binding?.colorRecycler?.adapter = adapter

        search(intent.getStringExtra("COLOR")!!)
    }

    private fun search(query: String) {
        imageData.clear()
        adapter?.notifyDataSetChanged()
        fetch = Fetch(this,
            object : Image {
                override fun loaded(data: ArrayList<Pexel>) {
                    val l = data.size
                    imageData.addAll(data)
                    adapter?.notifyItemRangeInserted(l, data.size)
                    adapter?.notifyDataSetChanged()
                    processing = false
                }
            }, "https://api.pexels.com/v1/search?query=Nature&color=$query&per_page=20")
            .also { it.getData() }
    }
}