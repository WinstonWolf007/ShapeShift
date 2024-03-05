package com.example.shapeshift

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.util.Locale

class SettingsActivity : AppCompatActivity() {
    // Constants for themes and languages
    private val availableThemes: List<String> = listOf("dark", "light", "default")
    private val availableLanguages: List<String> = listOf("français", "english")

    // Indexes for the selected language and theme
    private var languageIndex: Int = 1
    private var themeIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize activity
        initializeMainActivity()
        updateAppTheme()
        updateAppLanguage()
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

        languageTextView.setOnClickListener {
            if (languageIndex + 1 < availableLanguages.size) {
                languageIndex++
            } else {
                languageIndex = 0
            }

            val selectedLanguage = availableLanguages[languageIndex]

            languageTextView.text = selectedLanguage.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }

            UPDATE_JSON_FILE_OBJECT_INTERN_STORAGE(
                this,
                "default-settings.json",
                listOf("language", selectedLanguage)
            )
        }
    }

    // Update the application theme
    private fun updateAppTheme() {
        val themeTextView = findViewById<TextView>(R.id.themeInput)

        themeTextView.setOnClickListener {
            if (themeIndex + 1 < availableThemes.size) {
                themeIndex++
            } else {
                themeIndex = 0
            }

            val selectedTheme = availableThemes[themeIndex]
            themeTextView.text = selectedTheme
        }
    }
}
