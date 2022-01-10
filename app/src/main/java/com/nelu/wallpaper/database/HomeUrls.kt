package com.nelu.wallpaper.database

import android.content.Context

class HomeUrls (val context: Context) {

    companion object {
        private const val DATA_NAME = "APPLICATION"
        private const val IMAGE = "IMAGE"
        private const val NAME = "NAME"
    }

    var name: String
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getString(NAME, "Untitled")!!
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putString(NAME, value)
            editor.apply()
        }

    var image: String
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getString(IMAGE, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTXwVMBH75TaekX3rtcgkfKaoMOUHvTc2C17g&usqp=CAU")!!
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putString(IMAGE, value)
            editor.apply()
        }
}