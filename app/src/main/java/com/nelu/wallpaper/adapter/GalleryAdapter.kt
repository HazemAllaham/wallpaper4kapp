package com.nelu.wallpaper.adapter

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.nelu.wallpaper.R
import java.util.*

class GalleryAdapter(val context: Context,
                     val arrayList: ArrayList<GalleryModel>) : BaseAdapter() {
    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Any {
        return arrayList[position]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (convertView == null)
            view = LayoutInflater.from(context)
                .inflate(
                    R.layout.layout_gallery
                    , parent, false)

        Glide.with(context).load(arrayList[position].uri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(view!!.findViewById(R.id.image))

        view.findViewById<ImageView>(R.id.check)
            .visibility = if (arrayList[position].selected)
                View.VISIBLE else View.GONE

//        view.findViewById<ImageView>(R.id.video_icon).visibility =
//            if (video) View.VISIBLE else View.GONE

        return view
    }
}