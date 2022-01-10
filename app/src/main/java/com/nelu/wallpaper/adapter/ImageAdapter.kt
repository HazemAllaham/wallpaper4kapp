package com.nelu.wallpaper.adapter

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.nelu.wallpaper.R
import com.nelu.wallpaper.`interface`.Adapter
import com.nelu.wallpaper.activity.ImageActivity
import com.nelu.wallpaper.model.Pexel
import com.nelu.wallpaper.sql.Bookmark
import java.lang.Exception

class ImageAdapter(val context: Context, val data: ArrayList<Pexel>,
                   val adapter: Adapter, val layout: Int)
    : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val bookmark: ImageView = view.findViewById(R.id.bookmark)
        val progress: ProgressBar = view.findViewById(R.id.progress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(context)
                .inflate(
                    layout,
                    parent, false
                )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.clipToOutline = true
        holder.itemView.clipToOutline = true

        holder.progress.visibility = View.VISIBLE
        Glide.with(context).load(data[position].large)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean): Boolean {
                    context as Activity
                    context.runOnUiThread {
                        holder.progress.visibility = View.GONE
                    }
                    return false
                }
            }).into(holder.image)

        holder.image.setOnClickListener {
            ImageActivity.launch(context, data[position])
        }

        holder.bookmark.setImageDrawable(
            ResourcesCompat.getDrawable(
                context.resources,
                Bookmark(context).readDataAt(data[position].id)
                    ?.let { R.drawable.ic_baseline_bookmark_24
                    }?: R.drawable.ic_outline_bookmark_border_24,
                null
            )
        )

        holder.bookmark.setOnClickListener {
            Bookmark(context).readDataAt(data[position].id)
                ?.let { Bookmark(context).deleteAt(data[position].id)
                }?: Bookmark(context).insertData(data[position])
            holder.bookmark.setImageDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    Bookmark(context).readDataAt(data[position].id)
                        ?.let { R.drawable.ic_baseline_bookmark_24
                        }?: R.drawable.ic_outline_bookmark_border_24,
                    null
                )
            )
        }

        if (position+1==data.size) adapter.last()
    }

    override fun getItemCount(): Int {
        return data.size
    }
}