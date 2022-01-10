package com.nelu.wallpaper.sql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.nelu.wallpaper.model.Pexel

class Bookmark (context: Context) :
    SQLiteOpenHelper(context, "bookmark.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            ("CREATE TABLE "
                    + "Bookmark"
                    ) + " (id INTEGER PRIMARY KEY, " +
                    "large TEXT, original TEXT, " +
                    "photographer TEXT, " +
                    "photographerUrl TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Bookmark")
        onCreate(db)
    }

    fun insertData(data: Pexel): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("id", data.id)
        contentValues.put("large", data.large)
        contentValues.put("original", data.orginal)
        contentValues.put("photographer", data.photographer)
        contentValues.put("photographerUrl", data.photographerUrl)
        db.insert("Bookmark", null, contentValues)
        return true
    }

    fun readData(): ArrayList<Pexel> {
        val db = this.readableDatabase
        val selectQuery = "SELECT  * FROM Bookmark"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        val data = ArrayList<Pexel>()
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Pexel(
                        id = cursor.getInt(0),
                        large = cursor.getString(1),
                        orginal = cursor.getString(2),
                        photographer = cursor.getString(3),
                        photographerUrl = cursor.getString(4)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun deleteAt(pos: Int): Boolean {
        return this.writableDatabase.delete("Bookmark", "id=?", arrayOf("$pos")) > 0
    }

    fun readDataAt(pos: Int): Pexel? {
        val db = this.readableDatabase
        var data : Pexel? = null
        val selectQuery = "SELECT  * FROM Bookmark WHERE id == $pos"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            data = Pexel(
                id = cursor.getInt(0),
                large = cursor.getString(1),
                orginal = cursor.getString(2),
                photographer = cursor.getString(3),
                photographerUrl = cursor.getString(4)
            )
        }
        cursor.close()
        return data
    }
}