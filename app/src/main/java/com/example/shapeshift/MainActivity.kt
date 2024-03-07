package com.example.shapeshift

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.example.shapeshift.databases.DatabaseManager
import com.example.shapeshift.databases.Setting
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var dateCode: List<String>
    private lateinit var dateCodeDateCheck: List<String>
    private lateinit var today_plan_settings: JSONObject
    private lateinit var settings: JSONObject
    private val availableThemes: List<String> = listOf("dark", "light", "default")

    // Main database for every activities
    private val dbManager = DatabaseManager(this)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadSettingObject()
        setLocaleLanguage(settings.getString("language"), this)
        setContentView(R.layout.activity_main)

        dateCode = listOf(
            getText(R.string.week_su).toString(),
            getText(R.string.week_mo).toString(),
            getText(R.string.week_tu).toString(),
            getText(R.string.week_we).toString(),
            getText(R.string.week_th).toString(),
            getText(R.string.week_fr).toString(),
            getText(R.string.week_sa).toString()
        )

        dateCodeDateCheck = listOf(
            "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"
        )


//        dbManager.clearEveryData()
//
//        dbManager.insertSetting(
//            Setting(1, "json",
//                GET_JSON_FILE_OBJECT(this, "default-settings.json").toString()
//            )
//        )


        // Section in main activity
        this.ADD_WELCOME_TITLE()
        this.ADD_CALENDAR_SECTION()
        this.ADD_TODAY_PLAN()
        this.ADD_DETAIL_TODAY_PLAN()
        this.ADD_TODAY_PLAN_TEXT_VIEW()

        this.SET_THEME_TO_ACTIVITY()

        // Events
        this.CHANGE_TO_PLAN_ACTIVITY()
        this.CHANGE_TO_WORKOUT_ACTIVITY()
        this.CHANGE_TO_EDIT_ACTIVITY()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun SET_THEME_TO_ACTIVITY() {
        val defaultTheme: String = settings.getString("theme")

        val blackElementsTextView = listOf(
            findViewById<TextView>(R.id.home_welcome_title),
            findViewById<TextView>(R.id.week_title),
            findViewById<TextView>(R.id.today_plan_subtitle)
        )

        val page = findViewById<ConstraintLayout>(R.id.page)
        val plan_today = findViewById<FrameLayout>(R.id.plan_today_frame_layout)
        val weekParent = findViewById<RelativeLayout>(R.id.week_grid)
        val todayPlanDetail = findViewById<RelativeLayout>(R.id.today_plan_detail_list)

        if (defaultTheme == availableThemes[0]) {// dark light default
            page.setBackgroundColor(getColor(R.color.dark_gray))
            plan_today.setBackgroundResource(R.drawable.home_plan_component_white)
            for (detail in todayPlanDetail.children) {
                if (detail is TextView) {
                    detail.setTextColor(getColor(R.color.white))
                }
            }
            for (element in blackElementsTextView) {
                element.setTextColor(getColor(R.color.white))
            }
            for (child in weekParent.children) {
                if (child is TextView) {
                    if (dateCode[getTodayDateCode(dateCode)[1].toInt()] == child.text) {
                        child.setTextColor(getColor(R.color.orange))
                        child.setBackgroundResource(R.drawable.border_background_2_white)
                    } else {
                        child.setTextColor(getColor(R.color.white))
                        child.setBackgroundResource(R.drawable.border_background_white)
                    }
                }
            }
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

    private fun loadSettingObject() {
        val dbManager = DatabaseManager(this)
        val setting: Setting? = dbManager.getSpecificSetting("json")
        if (setting != null) {
            settings = JSONObject(setting.value)
        }
    }

    private fun ADD_TODAY_PLAN_TEXT_VIEW() {
        val textView = findViewById<TextView>(R.id.today_plan_title)
        var title: String

        if (today_plan_settings.length() != 0) {
            title = today_plan_settings.getString("name").uppercase()

            if (title.length > 18)
                title = title.substring(0, 18)
        }

        else {
            title = getText(R.string.dayOff).toString()
        }

        textView.text = title
    }

    private fun ADD_DETAIL_TODAY_PLAN() {
        if (today_plan_settings.length() != 0) {
            val relativeLayout = findViewById<RelativeLayout>(R.id.today_plan_detail_list)
            val listOfExercises = today_plan_settings.getJSONArray("workout")

            for (exerciseIDX in 0 until listOfExercises.length()) {
                val exerciseJSON = JSONObject(listOfExercises[exerciseIDX].toString())
                val exerciseName = exerciseJSON.getString("name")
                var exerciseRep = exerciseJSON.getString("rep")
                var exerciseSeries = exerciseJSON.getString("series")

                for (changeCell in 0 until 2) {
                    var text = exerciseName

                    if (exerciseName.length > 16)
                        text.substring(0, 16)

                    val layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    )

                    val textView = TextView(this)
                    textView.typeface = Typeface.createFromAsset(assets, "font/jb.ttf")

                    if (changeCell != 0) {
                        if (exerciseSeries.toInt() > 99) {
                            exerciseSeries = "99"
                        }

                        if (exerciseRep.toInt() > 99) {
                            exerciseRep = "99"
                        }

                        text = "$exerciseSeries x $exerciseRep"
                        textView.typeface = Typeface.createFromAsset(assets, "font/jb_bold.ttf")
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
                    }

                    textView.text = text
                    textView.textSize = 20F
                    textView.setTextColor(ContextCompat.getColor(this, R.color.black))
                    layoutParams.topMargin = exerciseIDX * 150
                    textView.layoutParams = layoutParams

                    relativeLayout.addView(textView)
                }
            }
        }
    }

    private fun ADD_WELCOME_TITLE() {
        val jsonObject: JSONObject =
            GET_JSON_FILE_OBJECT(application, "default-settings.json")

        val textView = findViewById<TextView>(R.id.home_welcome_title)
        var username: String = jsonObject.getString("username").uppercase()

        if (username.length > 10)
            username = username.substring(0, 10)
        val title = getText(R.string.home_welcome).toString() + " " + username
        textView.text = title
    }

    private fun CHANGE_TO_EDIT_ACTIVITY() {
        val textView = findViewById<TextView>(R.id.edit_text)

        textView.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("src", this::class.java.name)
            startActivity(intent)
        }
    }

    private fun CHANGE_TO_PLAN_ACTIVITY() {
        val textView = findViewById<TextView>(R.id.more_plan_btn)

        textView.setOnClickListener {
            val intent = Intent(this, AvailableWorkoutActivity::class.java)
            intent.putExtra("src", this::class.java.name)
            startActivity(intent)
        }
    }

    private fun CHANGE_TO_WORKOUT_ACTIVITY() {
        val textView = findViewById<FrameLayout>(R.id.plan_today_frame_layout)

        if (today_plan_settings.length() != 0) {
            textView.setOnClickListener {
                val intent = Intent(this, WorkoutActivity::class.java)
                intent.putExtra("src", this::class.java.name)
                intent.putExtra("plan", today_plan_settings.toString())
                startActivity(intent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ADD_CALENDAR_SECTION() {
        val relativeLayout = findViewById<RelativeLayout>(R.id.week_grid)

        for ((i, item) in dateCode.withIndex()) {
            val textView = TextView(this)

            // props
            textView.text = dateCode[i]
            textView.x = 145f * i
            textView.y = 50f
            textView.width = 90
            textView.height = 90
            textView.gravity = Gravity.CENTER

            textView.setTextColor(getColor(R.color.black))
            textView.setBackgroundResource(R.drawable.border_background)

            if (dateCode[getTodayDateCode(dateCode)[1].toInt()] == item) {
                textView.setTextColor(getColor(R.color.white))
                textView.setBackgroundResource(R.drawable.border_background_2)
            }

            relativeLayout.addView(textView)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ADD_TODAY_PLAN() {
        val planObject: JSONObject = GET_JSON_FILE_OBJECT(application, "default-settings.json")
        val listOfPlans: JSONArray = planObject.getJSONArray("plans")

        today_plan_settings = JSONObject("{}")

        for (i in 0 until listOfPlans.length()) {
            val planSettings = JSONObject(listOfPlans[i].toString())
            if (planSettings.getString("time") == getTodayDateCode(dateCodeDateCheck)[0]) {
                today_plan_settings = planSettings
                break
            }
        }
    }
}
