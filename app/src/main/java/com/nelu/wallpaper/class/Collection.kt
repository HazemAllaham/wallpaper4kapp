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

class Collection(val context: Context, val image: Image, imgUrl: String) {

    var url = imgUrl

    fun fetch() {
        val request: StringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                val data = ArrayList<Pexel>()
                try {
                    val jsonObject = JSONObject(response)
                    val jsonArray = jsonObject.getJSONArray("media")
                    val length = jsonArray.length()
                    for (i in 0 until length) {
                        val `object` = jsonArray.getJSONObject(i)
                        val objectImages = `object`.getJSONObject("src")
                        data.add(
                            Pexel(
                                id = `object`.getInt("id"),
                                large = objectImages.getString("large"),
                                orginal = objectImages.getString("original"),
                                photographer = `object`.getString("photographer"),
                                photographerUrl = `object`.getString("photographer_url")
                            )
                        )
                    }
                    Log.e("Loaded", jsonObject.toString())
                    image.loaded(data)
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

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(request)
    }

    fun getCollectionList() {
        val request: StringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                val data = ArrayList<Pexel>()
                try {
                    val jsonObject = JSONObject(response)
                    val jsonArray = jsonObject.getJSONArray("collections")
                    val length = jsonArray.length()
                    for (i in 0 until length) {
                        val `object` = jsonArray.getJSONObject(i)
                        Log.e("ob", `object`.toString())
                        data.add(
                            Pexel(
                                id = 0,
                                large = `object`.getString("title"),
                                orginal = `object`.getString("id"),
                                photographer = "",
                                photographerUrl = ""
//                                photographer = `object`.getString("photographer")
                            )
                        )
                    }
                    image.loaded(data)
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
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(request)
    }

    companion object {
        private const val api = "563492ad6f917000010000016dd6bd23c7eb4c0a924da4f8d03cd7c5" //My
//        private const val api = "563492ad6f917000010000011069c623c7ad4919b7f934771d8adbe9" //Git
    }
}