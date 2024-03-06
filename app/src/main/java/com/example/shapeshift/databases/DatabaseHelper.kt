package com.example.shapeshift.databases


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_SETTINGS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "settings"
        private const val CREATE_TABLE_SETTINGS = "CREATE TABLE settings (id INTEGER PRIMARY KEY, setting_key TEXT, setting_value TEXT)"
    }
}
