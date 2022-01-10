package com.nelu.wallpaper.external

import android.app.Activity
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nelu.wallpaper.R
import com.nelu.wallpaper.`interface`.Selected
import com.nelu.wallpaper.adapter.GalleryAdapter
import com.nelu.wallpaper.adapter.GalleryModel
import java.util.ArrayList

class ImageSelector(val activity: Activity, val selected: Selected) {

    fun imageSelect() {

        val imageSheet = BottomSheetDialog(activity, R.style.bottomSheet)
        imageSheet.setContentView(R.layout.image_sheet)

        imageSheet.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout = bottomSheetDialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )

            parentLayout?.let { layout ->
                val behaviour = BottomSheetBehavior.from(layout)
                val layoutParams = layout.layoutParams
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                layout.layoutParams = layoutParams

                behaviour.setBottomSheetCallback(
                    object : BottomSheetBehavior.BottomSheetCallback() {

                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                                (behaviour as BottomSheetBehavior<*>).state =
                                    BottomSheetBehavior.STATE_EXPANDED
                            }
                        }

                        override fun onSlide(bottomSheet: View, slideOffset: Float) {
                            Log.e("TAG", "Slide")
                        }
                    }
                )

                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        val data = fetchGalleryImages(activity)

        val grid = imageSheet.findViewById<GridView>(R.id.image_grid)

        grid?.adapter = GalleryAdapter(activity, data)
        grid?.onItemClickListener = AdapterView
            .OnItemClickListener { _, _, position, _ ->
                selected.image(data[position].uri)
                imageSheet.dismiss()
            }

        imageSheet.findViewById<ImageView>(R.id.back)?.setOnClickListener {
            imageSheet.dismiss()
        }
        imageSheet.show()
    }

    private fun fetchGalleryImages(context: Activity): ArrayList<GalleryModel> {
        val columns = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID
        ) //get all columns of type images

        val orderBy = MediaStore.Images.Media.DEFAULT_SORT_ORDER //order data by date
        val imagecursor: Cursor = context.managedQuery(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
            null, "$orderBy DESC"
        ) //get all data in Cursor by sorting in DESC order
        val galleryImageUrls: ArrayList<GalleryModel> = ArrayList()
        for (i in 0 until imagecursor.count) {
            imagecursor.moveToPosition(i)
            val dataColumnIndex: Int =
                imagecursor.getColumnIndex(MediaStore.Images.Media.DATA) //get column index
            galleryImageUrls.add(
                GalleryModel(
                    imagecursor.getString(dataColumnIndex)
                    , false
                )
            ) //get Image from column index
        }
        return galleryImageUrls
    }
}