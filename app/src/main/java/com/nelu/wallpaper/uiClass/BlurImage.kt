package com.nelu.wallpaper.uiClass

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

class BlurImage internal constructor(private val context: Context) {

    private var image: Bitmap? = null
    private var intensity = 08f
    private val MAX_RADIUS = 25f
    private val MIN_RADIUS = 0f
    private var async = false

    /*
     *  This method is creating a blur bitmap , this method use renderscript which efficient
     *  and here we use scriptIntrinsicBlur for performing blurring
     * */
    private fun blur(): Bitmap? {
        if (image == null) {
            return image
        }
        val width = image!!.width.toFloat().roundToInt()
        val height = image!!.height.toFloat().roundToInt()
        val input = Bitmap.createScaledBitmap(image!!, width, height, false)
        val output = Bitmap.createBitmap(input)
        val rs = RenderScript.create(
            context
        )
        val intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val inputAllocation = Allocation.createFromBitmap(rs, input)
        val outputAllocation = Allocation.createFromBitmap(rs, output)
        intrinsicBlur.setRadius(intensity)
        intrinsicBlur.setInput(inputAllocation)
        intrinsicBlur.forEach(outputAllocation)
        outputAllocation.copyTo(output)
        return output
    }

    /*
     * Here we get bitmap on which we apply the blur process
     * */
    fun load(bitmap: Bitmap?): BlurImage {
        image = bitmap
        return this
    }

    fun load(res: Int): BlurImage {
        image = BitmapFactory.decodeResource(context.resources, res)
        return this
    }

    fun intensity(intensity: Float): BlurImage {
        if (intensity < MAX_RADIUS && intensity > 0) this.intensity =
            intensity else this.intensity = MAX_RADIUS
        return this
    }

    fun async(async: Boolean): BlurImage {
        this.async = async
        return this
    }

    fun into(img: ImageView) {
        if (async) {
            CoroutineScope(Dispatchers.Default).launch {
                val bitmap = blur()
                val weakReference: WeakReference<ImageView> = WeakReference(img)
                val imageView = weakReference.get()
                if (imageView != null && bitmap != null) {
                    (context as Activity).runOnUiThread {
                        imageView.setImageBitmap(bitmap)
                    }
                }
            }
        } else {
            try {
                img.setImageBitmap(blur())
            } catch (ex: NullPointerException) {
                ex.printStackTrace()
            }
        }
    }

    val imageBlur: Bitmap?
        get() = blur()


    companion object {
        private const val BITMAP_SCALE = 0.3f
        private const val BLUR_RADIUS = 7f
        fun with(context: Context): BlurImage {
            return BlurImage(context)
        }
    }
}