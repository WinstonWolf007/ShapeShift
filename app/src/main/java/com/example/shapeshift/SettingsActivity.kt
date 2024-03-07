package com.example.shapeshift

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.shapeshift.databases.DatabaseManager
import com.example.shapeshift.databases.Setting
import org.json.JSONObject
import java.util.Locale


data class TranslateTheme(val dark: String, val light: String, val default: String)

class SettingsActivity : AppCompatActivity() {
    // Constants for themes and languages
    private val availableThemes: List<String> = listOf("dark", "light", "default")
    private val availableLanguages: List<String> = listOf("fr", "en")

    // Indexes for the selected language and theme
    private var languageIndex: Int = 0
    private var themeIndex: Int = 0

    // Database
    private lateinit var settings: JSONObject
    private val dbManager = DatabaseManager(this)

    // Translate Text
    private lateinit var translateTheme: TranslateTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadSettingObject()

        setLocaleLanguage(settings.getString("language"), this)

        translateTheme = TranslateTheme(
            dark = getString(R.string.dark),
            light = getString(R.string.light),
            default = getString(R.string.defaultTheme)
        )

        setContentView(R.layout.activity_settings)

        changeColor()

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

    fun setLocaleLanguage(languageCode: String, context: Context) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        context.createConfigurationContext(configuration)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
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
            this.recreate()
        }
    }

    // Update the application theme
    private fun updateAppTheme() {
        val themeTextView = findViewById<TextView>(R.id.themeInput)

        // set default theme
        val defaultTheme: String = settings.getString("theme")

        when (defaultTheme) {
            availableThemes[0] -> themeTextView.text = translateTheme.dark
            availableThemes[1] -> themeTextView.text = translateTheme.light
            availableThemes[2] -> themeTextView.text = translateTheme.default
        }

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
            this.recreate()
        }
    }

    fun changeColor() {
        val defaultTheme: String = settings.getString("theme")

        val blackElements = listOf(
            findViewById<TextView>(R.id.textView),
            findViewById<TextView>(R.id.languageInput),
            findViewById<TextView>(R.id.themeInput),
            findViewById<TextView>(R.id.textView4)
        )

        val whiteElement = findViewById<ConstraintLayout>(R.id.page)

        if (defaultTheme == availableThemes[0]) {// dark light default
            whiteElement.setBackgroundColor(resources.getColor(R.color.dark_gray))
            for (element in blackElements) {
                element.setTextColor(resources.getColor(R.color.white))
            }
        }
    }
}
