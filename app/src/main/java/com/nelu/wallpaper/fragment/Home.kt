package com.nelu.wallpaper.fragment

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.nelu.wallpaper.*
import com.nelu.wallpaper.`class`.Collection
import com.nelu.wallpaper.`class`.Fetch
import com.nelu.wallpaper.`interface`.Adapter
import com.nelu.wallpaper.`interface`.Image
import com.nelu.wallpaper.`interface`.Selected
import com.nelu.wallpaper.activity.ColorActivity
import com.nelu.wallpaper.activity.SearchActivity
import com.nelu.wallpaper.adapter.ImageAdapter
import com.nelu.wallpaper.adapter.PagerAdapter
import com.nelu.wallpaper.database.HomeUrls
import com.nelu.wallpaper.databinding.FragmentHomeBinding
import com.nelu.wallpaper.external.ImageSelector
import com.nelu.wallpaper.model.Pexel
import java.lang.Exception
import java.nio.channels.Selector

class Home : Fragment() {

    private var processing = false
    private var fetch: Fetch? = null
    private val imageData = ArrayList<Pexel>()
    private lateinit var adapter: ImageAdapter
    private var selector: ImageSelector? = null
    private var binding: FragmentHomeBinding? = null



    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                selector?.imageSelect()
            } else {
                Toast.makeText(requireContext(),
                    "Permission Denied"
                    , Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding?.search?.clipToOutline = true
        initFragments()

        adapter = ImageAdapter(requireContext(), imageData,
            object : Adapter {
                override fun last() {
//                    if (!processing) {
//                        processing = true
//                        fetch?.getData()
//                    }
                }
            }, R.layout.layout_popular
        )

        binding?.popularRecycler?.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )
        binding?.popularRecycler?.adapter = adapter

        fetch = Fetch(requireContext(),
            object : Image {
                override fun loaded(data: ArrayList<Pexel>) {
                    processing = false
                    imageData.addAll(data)
                    adapter.notifyDataSetChanged()
                }
            }, "https://api.pexels.com/v1/search?query=Nature&per_page=15")
        fetch?.getData()

        binding?.search?.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    SearchActivity::class.java
                )
            )
        }

        binding?.name?.text = HomeUrls(requireContext()).name

        binding?.name?.setOnClickListener {
            setNameImg()
        }

        binding?.profileImg?.setOnClickListener {
            setNameImg()
        }

        Glide.with(this).load(HomeUrls(requireContext()).image)
            .centerCrop().into(binding!!.profileImg)

        initColorClicks()

        return binding!!.root
    }

    private fun setNameImg() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.layout_imge)

        val image = dialog.findViewById<ImageView>(R.id.img)
        val name = dialog.findViewById<EditText>(R.id.name_edit)

        Glide.with(requireContext()).load(
            HomeUrls(requireContext()).image
        ).centerCrop().into(image)

        selector = ImageSelector(requireActivity()
            , object : Selected {
                override fun image(url: String) {
                    Glide.with(requireContext()).load(
                        url).centerCrop().into(image)
                    HomeUrls(requireContext()).image = url
                }
            })

        image.setOnClickListener {
            requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
        }

        dialog.findViewById<Button>(R.id.cancel)
            .setOnClickListener {
                dialog.dismiss()
            }
        dialog.findViewById<Button>(R.id.submit)
            .setOnClickListener {
                if (name.text.toString().isEmpty())
                    Toast.makeText(requireContext(),
                        "Name cannot be empty",
                        Toast.LENGTH_SHORT).show()
                else {
                    HomeUrls(requireContext()).name = name.text.toString()
                    dialog.dismiss()
                    Glide.with(this).load(HomeUrls(requireContext()).image)
                        .centerCrop().into(binding!!.profileImg)
                    binding?.name?.text = name.text.toString()
                }
            }
        dialog.show()
    }

    private fun initColorClicks() {
        binding?.red?.setOnClickListener {
            ColorActivity.launch(requireContext(), "Red")
        }
        binding?.green?.setOnClickListener {
            ColorActivity.launch(requireContext(), "Green")
        }
        binding?.blue?.setOnClickListener {
            ColorActivity.launch(requireContext(), "Blue")
        }
        binding?.orange?.setOnClickListener {
            ColorActivity.launch(requireContext(), "Orange")
        }
        binding?.yellow?.setOnClickListener {
            ColorActivity.launch(requireContext(), "Yellow")
        }
        binding?.white?.setOnClickListener {
            ColorActivity.launch(requireContext(), "White")
        }
        binding?.purple?.setOnClickListener {
            ColorActivity.launch(requireContext(), "Purple")
        }
    }

    private fun initFragments() {
        Collection(requireContext(),
            object : Image {
                override fun loaded(data: ArrayList<Pexel>) {
                    val pagerAdapter = PagerAdapter(
                        requireActivity().supportFragmentManager, 1
                    )
                    binding?.tabLayout?.run {
                        data.reversed().forEach {
                            addTab(newTab().setText(it.large))
                            pagerAdapter.addFragment(Collection(it.orginal))
                        }
                    }
                    binding?.viewPager?.adapter = pagerAdapter
                    binding?.viewPager?.offscreenPageLimit = data.size
                    binding?.tabLayout?.tabGravity = TabLayout.GRAVITY_FILL

                    binding?.viewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding!!.tabLayout))

                    binding?.tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                        override fun onTabReselected(tab: TabLayout.Tab?) { /****/ }

                        override fun onTabUnselected(tab: TabLayout.Tab?) { /****/ }

                        override fun onTabSelected(tab: TabLayout.Tab) {
                            binding?.viewPager?.currentItem = tab.position
                        }
                    })
                }
            }, "https://api.pexels.com/v1/collections?per_page=15"
        ).getCollectionList()
    }

    private fun initFragments_2() {
        binding?.tabLayout?.run {
            addTab(newTab().setText("Featured"))
            addTab(newTab().setText("CityScape"))
            addTab(newTab().setText("Portrait"))
            addTab(newTab().setText("Abstract"))
            addTab(newTab().setText("Inspiration"))
            addTab(newTab().setText("Black"))
        }

        binding?.viewPager?.adapter = PagerAdapter(
            requireFragmentManager(), 1
        ).apply {
            addFragment(Featured())
            addFragment(Cityscape())
            addFragment(Portrait())
            addFragment(Abstract())
            addFragment(Inspiration())
            addFragment(Black())
        }

        binding?.viewPager?.offscreenPageLimit = 5
        binding?.tabLayout?.tabGravity = TabLayout.GRAVITY_FILL

        binding?.viewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding!!.tabLayout))

        binding?.tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) { /****/ }

            override fun onTabUnselected(tab: TabLayout.Tab?) { /****/ }

            override fun onTabSelected(tab: TabLayout.Tab) {
                binding?.viewPager?.currentItem = tab.position
            }
        })
    }
}