package com.example.shapeshift

import android.content.Context
import org.json.JSONObject

fun GET_JSON_FILE_OBJECT(context: Context, jsonFileName: String): JSONObject {
    val jsonFileString: String =
        context.assets.open("json/$jsonFileName").bufferedReader().use { it.readText() }
    return JSONObject(jsonFileString)
}

fun UPDATE_JSON_STRING(jsonString: String, vararg params: List<String>): String {
    val jsonObject = JSONObject(jsonString)

    for (paramList in params) {
        if (paramList.size >= 2) {
            val key = paramList[0]
            val value = paramList[1]
            jsonObject.put(key, value)
        }
    }
    return jsonObject.toString()
}

