package com.example.shapeshift

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.text.capitalize
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.IOException
import java.util.Locale

class Edit : AppCompatActivity() {
    val theme: List<String> = listOf("dark", "light", "default")
    var theme_cursor: Int = 0

    val language: List<String> = listOf("français", "english")
    var language_cursor: Int = 1

    private lateinit var defaultSettingsJsonObject: JSONObject

    private fun loadJson() {
        val jsonObject: JSONObject? = GET_JSON_FILE_OBJECT_INTERN_STORAGE(this, "default-settings.json")
        if (jsonObject != null) {
            defaultSettingsJsonObject = jsonObject
        }
    }

    override fun onResume() {
        super.onResume()
        this.loadJson()
        Log.d("UBERT", "OnResume(): $defaultSettingsJsonObject")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // Always load JSON data when activity is created
        loadJson()

        // Init Activity
        CHANGE_TO_MAIN_ACTIVITY()
        UPDATE_APP_THEME()
        UPDATE_APP_LANGUAGE()

        Log.d("UBERT", "onCreate(): $defaultSettingsJsonObject")
    }


    private fun CHANGE_TO_MAIN_ACTIVITY() {
        val textView = findViewById<TextView>(R.id.exit_text)

        textView.setOnClickListener {
            // Save the updated JSON data before starting MainActivity
            saveJsonToFile()

            Log.d("UBERT", "Change Activity: $defaultSettingsJsonObject")

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveJsonToFile() {
        // Convert the JSON object to a string
        val jsonString = defaultSettingsJsonObject.toString()

        try {
            // Write the JSON string to the file
            val fileOutputStream = openFileOutput("default-settings.json", Context.MODE_PRIVATE)
            fileOutputStream.write(jsonString.toByteArray())
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun UPDATE_APP_LANGUAGE() {
        val textView = findViewById<TextView>(R.id.languageInput)

        val languages = defaultSettingsJsonObject.getString("language").capitalize()
        textView.text = languages

        textView.setOnClickListener {
            if (language_cursor+1 < language.size) {
                language_cursor++
            } else {
                language_cursor = 0
            }

            val language_selected = language[language_cursor]

            textView.text = language_selected.capitalize()

            UPDATE_JSON_FILE_OBJECT_INTERN_STORAGE(this, "default-settings.json",
                listOf("language", language_selected)
            )

            this.loadJson()

            Log.d("UBERT", "UpdateAppLanguage: $defaultSettingsJsonObject")
        }
    }

    private fun UPDATE_APP_THEME() {
        val textView = findViewById<TextView>(R.id.themeInput)

        textView.setOnClickListener {
            if (theme_cursor+1 < theme.size) {
                theme_cursor++
            } else {
                theme_cursor = 0
            }

            val themeType = theme[theme_cursor]
            textView.text = themeType

//            if (themeType === theme[0]) {// Dark..
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            }
//
//            else if (themeType === theme[1]) {// Light...
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            }
        }
    }
}