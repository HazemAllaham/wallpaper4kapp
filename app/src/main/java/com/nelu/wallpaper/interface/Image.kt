package com.nelu.wallpaper.`interface`

import com.nelu.wallpaper.model.Pexel

interface Image {
    fun loaded(data: ArrayList<Pexel>)
}