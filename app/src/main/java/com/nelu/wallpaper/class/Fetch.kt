package com.nelu.wallpaper.`class`

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.nelu.wallpaper.`interface`.Image
import com.nelu.wallpaper.model.Pexel
import org.json.JSONException
import org.json.JSONObject

class Fetch(val context: Context, val image: Image, imgUrl: String) {

    var url = imgUrl

    fun getData() {
        val request: StringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                val data = ArrayList<Pexel>()
                try {
                    val jsonObject = JSONObject(response)
                    Log.e("TAG", jsonObject.toString())
                    val jsonArray = jsonObject.getJSONArray("photos")
                    val length = jsonArray.length()
                    for (i in 0 until length) {
                        val `object` = jsonArray.getJSONObject(i)
                        val objectImages = `object`.getJSONObject("src")
                        data.add(
                            Pexel(
                                id = `object`.getInt("id"),
                                large = objectImages.getString("large"),
                                orginal = objectImages.getString("large2x"),
//                                orginal = objectImages.getString("original"),
                                photographer = `object`.getString("photographer"),
                                photographerUrl = `object`.getString("photographer_url")
                            )
                        )
                    }
                    image.loaded(data)
                    url = if (jsonObject.getString("next_page").isNullOrEmpty())
                        "" else jsonObject.getString("next_page")
                } catch (e: JSONException) {
                    Log.e("Error", e.toString())
                }
            }, Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = api
                return params
            }
        }

        if (url.isNotEmpty()) {
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(request)
        }
    }

    companion object {
        private const val api = "563492ad6f917000010000016dd6bd23c7eb4c0a924da4f8d03cd7c5" //My

         // 563492ad6f91700001000001c727ee9252184468999ec237e0111004
//        private const val api = "563492ad6f917000010000011069c623c7ad4919b7f934771d8adbe9" //Git
    }
}