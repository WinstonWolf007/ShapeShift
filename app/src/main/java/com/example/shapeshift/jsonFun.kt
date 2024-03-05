package com.example.shapeshift

import android.app.Application
import android.content.Context
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


fun COPY_JSON_FILE_OBJECT(context: Context, jsonFileName: String) {
    val jsonFileObject = context.assets.open("json/$jsonFileName")
    val outputPath = context.openFileOutput(jsonFileName, Context.MODE_PRIVATE)
    jsonFileObject.copyTo(outputPath)
    jsonFileObject.close()
    outputPath.close()
}

fun GET_JSON_FILE_OBJECT_INTERN_STORAGE(context: Context, fileName: String): JSONObject? {
    try {
        val fileInputStream = context.openFileInput(fileName)
        val size = fileInputStream.available()
        val buffer = ByteArray(size)
        fileInputStream.read(buffer)
        fileInputStream.close()
        val jsonString = String(buffer, Charsets.UTF_8)
        return JSONObject(jsonString)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}


fun UPDATE_JSON_FILE_OBJECT_INTERN_STORAGE(context: Context, jsonFileName: String, vararg key_val: List<String>) {
    try {
        val fileInputStream = context.openFileInput(jsonFileName)
        val size = fileInputStream.available()
        val buffer = ByteArray(size)
        fileInputStream.read(buffer)
        fileInputStream.close()
        val jsonString = String(buffer, Charsets.UTF_8)

        val jsonObject = JSONObject(jsonString)

        for (item in key_val) {
            if (item.size == 2) {
                jsonObject.put(item[0], item[1])
            }
        }

        // Save the updated JSON string to the file
        val fileOutputStream = context.openFileOutput(jsonFileName, Context.MODE_PRIVATE)
        fileOutputStream.write(jsonObject.toString().toByteArray())
        fileOutputStream.close()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}



