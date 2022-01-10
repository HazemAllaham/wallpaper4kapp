package com.nelu.wallpaper.activity

import android.app.WallpaperManager
import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider.getUriForFile
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.nelu.wallpaper.R
import com.nelu.wallpaper.databinding.ActivityImageBinding
import com.nelu.wallpaper.model.Pexel
import com.nelu.wallpaper.sql.Bookmark
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.util.*
import kotlin.time.seconds

class ImageActivity : AppCompatActivity() {

    companion object {
        fun launch(context: Context, data: Pexel) {
            context.startActivity(
                Intent(
                    context,
                    ImageActivity::class.java
                ).putExtra("ORIGINAL", data.orginal)
                    .putExtra("LARGE", data.large)
                    .putExtra("PHOTOGRAPHER", data.photographer)
                    .putExtra("PHOTOGRAPHER_URL", data.photographerUrl)
                    .putExtra("ID", data.id)
            )
        }
    }

    private var lastClick: Long = 0
    private var imageBitmap: Bitmap? = null
    private lateinit var binding: ActivityImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.back.clipToOutline = true
        binding.share.clipToOutline = true
        binding.sheet.clipToOutline = true
        binding.download.clipToOutline = true
        binding.bookmark.clipToOutline = true
        binding.blurView.clipToOutline = true
        binding.setBackground.clipToOutline = true
        lastClick = System.currentTimeMillis()
        binding.back.setOnClickListener { finish() }
        initViews()

        binding.download.setOnClickListener {
            imageBitmap?.let {
                saveMediaToStorage(it)
            }?: Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }

        binding.setBackground.setOnClickListener {
            binding.setBackground.visibility = View.GONE
            binding.setUpProgress.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    WallpaperManager.getInstance(applicationContext).setBitmap(imageBitmap)
                    runOnUiThread {
                        binding.setUpProgress.visibility = View.GONE
                        binding.setBackground.visibility = View.VISIBLE
                        Toast.makeText(this@ImageActivity, "Wallpaper set!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: IOException) {
                    runOnUiThread {
                        binding.setUpProgress.visibility = View.GONE
                        binding.setBackground.visibility = View.VISIBLE
                        Toast.makeText(this@ImageActivity, "Error!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.share.setOnClickListener {
            shareImage()
        }

        binding.bookmark.setOnClickListener {
            Bookmark(this).readDataAt(intent.getIntExtra("ID", 0))
                ?.let { Bookmark(this).deleteAt(intent.getIntExtra("ID", 0))
                }?: Bookmark(this).insertData(addPexel())
            binding.bookmark.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    Bookmark(this).readDataAt(intent.getIntExtra("ID", 0))
                        ?.let { R.drawable.ic_baseline_bookmark_24
                        }?: R.drawable.ic_outline_bookmark_border_24,
                    null
                )
            )
        }

        binding.sheet.setOnClickListener {
            binding.sheet.isClickable = false
            if (binding.expLayout.isExpanded) {
                binding.expLayout.collapse(true)
            }
            else binding.expLayout.expand(true)
            binding.sheet.animate().apply {
                rotationBy(180f)
                duration = 500
            }.start()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.sheet.isClickable = true
            },750)
        }
    }

    private fun addPexel(): Pexel {
        return Pexel(
            id = intent.getIntExtra("ID", 0),
            large = intent.getStringExtra("LARGE")!!,
            orginal = intent.getStringExtra("ORIGINAL")!!,
            photographer = intent.getStringExtra("PHOTOGRAPHER")!!,
            photographerUrl = intent.getStringExtra("PHOTOGRAPHER_URL")!!
        )
    }

    private fun initViews() {
        imageBitmap = null
        val radius = 20f

        val decorView: View = window.decorView
        val windowBackground: Drawable = decorView.background

        binding.blurView.setupWith(decorView.findViewById(android.R.id.content))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setBlurAutoUpdate(true)
            .setHasFixedTransformationMatrix(true)

        Glide.with(this).load(intent.getStringExtra("LARGE"))
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
                    runOnUiThread {
                        binding.image.setImageDrawable(resource)
                        binding.blurView.visibility = View.VISIBLE
                        binding.loadingProg.visibility = View.GONE
                    }
                    imageBitmap = resource?.toBitmap()
                    return false
                }
            }).submit()/*into(binding.image)*/

        binding.pname.text = intent.getStringExtra("PHOTOGRAPHER")!!
        binding.pid.text = intent.getStringExtra("PHOTOGRAPHER_URL")!!.replace("https://www.pexels.com/", "")

        binding.bookmark.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                Bookmark(this).readDataAt(intent.getIntExtra("ID", 0))
                    ?.let { R.drawable.ic_baseline_bookmark_24
                    }?: R.drawable.ic_outline_bookmark_border_24,
                null
            )
        )

        Glide.with(this).load(intent.getStringExtra("LARGE"))
            .centerCrop().into(binding.profileImg)
    }

    fun saveMediaToStorage(bitmap: Bitmap) {
        //Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Saved to Photos", Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun shareImage() {
        binding.share.visibility = View.GONE
        binding.shareProgress.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Default).launch {
            imageBitmap?.let {
                val cachePath = File(externalCacheDir, "share/")
                cachePath.mkdirs()

                val file = File(cachePath, "share.png")

                val fileOutputStream: FileOutputStream
                try {
                    fileOutputStream = FileOutputStream(file)
                    it.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                    fileOutputStream.flush()
                    fileOutputStream.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val contentUri: Uri = getUriForFile(
                    this@ImageActivity,
                    applicationContext.packageName + ".provider", file)

                val intent = Intent(Intent.ACTION_SEND)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.putExtra(Intent.EXTRA_STREAM, contentUri)
                intent.type = "image/png"
                intent.clipData = ClipData.newRawUri("", contentUri)

                intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                startActivity(Intent.createChooser(intent, "Chooser Title"))

                Handler(Looper.getMainLooper()).postDelayed({
                    runOnUiThread {
                        binding.share.visibility = View.VISIBLE
                        binding.shareProgress.visibility = View.GONE
                    }
                },1000)
            }
        }
    }
}