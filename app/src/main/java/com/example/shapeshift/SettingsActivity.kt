package com.example.shapeshift

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.shapeshift.databases.DatabaseManager
import com.example.shapeshift.databases.Setting
import org.json.JSONObject

class SettingsActivity : AppCompatActivity() {
    // Constants for themes and languages
    private val availableThemes: List<String> = listOf("dark", "light", "default")
    private val availableLanguages: List<String> = listOf("français", "english")

    // Indexes for the selected language and theme
    private var languageIndex: Int = 1
    private var themeIndex: Int = 0

    private lateinit var settings: JSONObject

    private val dbManager = DatabaseManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        loadSettingObject()

        // Initialize activity
        initializeMainActivity()
        updateAppTheme()
        updateAppLanguage()
    }

    private fun loadSettingObject() {
        val dbManager = DatabaseManager(this)
        val setting: Setting? = dbManager.getSpecificSetting("json")
        if (setting != null) {
            settings = JSONObject(setting.value)
        }
    }

    // Navigate to the main activity
    private fun initializeMainActivity() {
        val exitTextView = findViewById<TextView>(R.id.exit_text)

        exitTextView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // Update the application language
    private fun updateAppLanguage() {
        val languageTextView = findViewById<TextView>(R.id.languageInput)

        // set default language
        val defaultLanguage: String = settings.getString("language")
        languageTextView.text = defaultLanguage
        languageIndex = availableLanguages.indexOf(defaultLanguage)

        languageTextView.setOnClickListener {
            if (languageIndex + 1 < availableLanguages.size) {
                languageIndex++
            } else {
                languageIndex = 0
            }

            val selectedLanguage = availableLanguages[languageIndex]

            // Refresh Database
            val jsonString: String =
                UPDATE_JSON_STRING(settings.toString(), listOf("language", selectedLanguage))
            dbManager.insertSetting(
                Setting(1, "json", jsonString)
            )

            languageTextView.text = selectedLanguage
        }
    }

    // Update the application theme
    private fun updateAppTheme() {
        val themeTextView = findViewById<TextView>(R.id.themeInput)

        // set default theme
        val defaultTheme: String = settings.getString("theme")
        themeTextView.text = defaultTheme
        themeIndex = availableThemes.indexOf(defaultTheme)


        themeTextView.setOnClickListener {
            if (themeIndex + 1 < availableThemes.size) {
                themeIndex++
            } else {
                themeIndex = 0
            }

            val selectedTheme = availableThemes[themeIndex]

            // Refresh Database
            val jsonString: String =
                UPDATE_JSON_STRING(settings.toString(), listOf("theme", selectedTheme))
            dbManager.insertSetting(
                Setting(1, "json", jsonString)
            )

            themeTextView.text = selectedTheme
        }
    }
}
