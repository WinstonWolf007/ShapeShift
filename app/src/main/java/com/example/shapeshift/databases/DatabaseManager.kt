package com.example.shapeshift.databases

import android.content.ContentValues
import android.content.Context

class DatabaseManager(context: Context) {

    private val dbHelper: DatabaseHelper = DatabaseHelper(context)

    fun insertSetting(setting: Setting) {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put("setting_key", setting.key)
            put("setting_value", setting.value)
        }

        val key = setting.key
        val cursor = db.rawQuery("SELECT * FROM settings WHERE setting_key = ?", arrayOf(key))

        if (cursor.moveToFirst()) {
            db.update("settings", contentValues, "setting_key = ?", arrayOf(key))
        } else {
            db.insert("settings", null, contentValues)
        }

        cursor.close()
        db.close()
    }


    fun clearEveryData() {
        val db = dbHelper.writableDatabase
        db.delete("settings", null, null)
        db.close()
    }

    fun getSpecificSetting(key: String): Setting? {
        val everySettings = getAllSettings()

        for (setting in everySettings) {
            if (setting.key == key) {
                return setting
            }
        }

        return null
    }

    fun getAllSettings(): List<Setting> {
        val settings = mutableListOf<Setting>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM settings", null)
        cursor.use { c ->
            while (c.moveToNext()) {
                val idIndex = c.getColumnIndex("id")
                val keyIndex = c.getColumnIndex("setting_key")
                val valueIndex = c.getColumnIndex("setting_value")
                if (idIndex != -1 && keyIndex != -1 && valueIndex != -1) {
                    val id = c.getLong(idIndex)
                    val key = c.getString(keyIndex)
                    val value = c.getString(valueIndex)
                    settings.add(Setting(id, key, value))
                } // else // columns not found
            }
        }
        db.close()
        return settings
    }
}
