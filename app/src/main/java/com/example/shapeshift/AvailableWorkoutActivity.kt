package com.example.shapeshift

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.shapeshift.databases.DatabaseManager
import com.example.shapeshift.databases.Setting
import org.json.JSONArray
import org.json.JSONObject

class AvailableWorkoutActivity : AppCompatActivity() {

    private lateinit var weeklyPlanList: MutableList<JSONObject>
    private val dayOfWeekCodes = listOf<String>("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
    private lateinit var settings: JSONObject
    private val availableThemes: List<String> = listOf("dark", "light", "default")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_available_workout)

        loadSettingObject()


        // load activity
        loadWeekPlan()
        addWorkoutLayout()

        SET_THEME_TO_ACTIVITY()

        // Set up event listener
        setMainActivityButtonListener()
    }

    private fun SET_THEME_TO_ACTIVITY() {
        val defaultTheme: String = settings.getString("theme")

        val page = findViewById<ConstraintLayout>(R.id.page)
        val topMenu = findViewById<RelativeLayout>(R.id.close_button)

        val planLayouts = listOf<FrameLayout>(
            findViewById(R.id.plan_frame_layout),
            findViewById(R.id.plan_frame_layout2),
            findViewById(R.id.plan_frame_layout3),
            findViewById(R.id.plan_frame_layout4),
            findViewById(R.id.plan_frame_layout5),
            findViewById(R.id.plan_frame_layout6)
        )

        val textLayouts = listOf<TextView>(
            findViewById(R.id.plan_title0),
            findViewById(R.id.plan_title1),
            findViewById(R.id.plan_title2),
            findViewById(R.id.plan_title3),
            findViewById(R.id.plan_title4),
            findViewById(R.id.plan_title5),
        )

        if (availableThemes[0] == defaultTheme) {
            page.setBackgroundColor(resources.getColor(R.color.dark_gray))
            for (element in planLayouts) {
                element.setBackgroundResource(R.drawable.home_plan_component_white)
            }
            for (text in textLayouts) {
                text.setTextColor(getColor(R.color.white))
            }
            topMenu.setBackgroundColor(getColor(R.color.light_gray))
        }
    }

    private fun loadSettingObject() {
        val dbManager = DatabaseManager(this)
        val setting: Setting? = dbManager.getSpecificSetting("json")
        if (setting != null) {
            settings = JSONObject(setting.value)
        }
    }

    private fun addWorkoutLayout() {
        val maxWorkoutCount = 6

        val workoutTitleViews = listOf<TextView>(
            findViewById(R.id.plan_title0),
            findViewById(R.id.plan_title1),
            findViewById(R.id.plan_title2),
            findViewById(R.id.plan_title3),
            findViewById(R.id.plan_title4),
            findViewById(R.id.plan_title5)
        )

        val workoutLayouts = listOf<FrameLayout>(
            findViewById(R.id.plan_frame_layout),
            findViewById(R.id.plan_frame_layout2),
            findViewById(R.id.plan_frame_layout3),
            findViewById(R.id.plan_frame_layout4),
            findViewById(R.id.plan_frame_layout5),
            findViewById(R.id.plan_frame_layout6)
        )

        for (index in 0 until maxWorkoutCount) {
            if (index < weeklyPlanList.size) {
                // Show workout layout and title
                workoutLayouts[index].visibility = View.VISIBLE
                workoutLayouts[index].isEnabled = true
                workoutTitleViews[index].visibility = View.VISIBLE
                workoutTitleViews[index].isEnabled = true
                workoutTitleViews[index].text = weeklyPlanList[index].getString("name")

                // Set click listener for each workout layout
                workoutLayouts[index].setOnClickListener {
                    val intent = Intent(this, WorkoutActivity::class.java)
                    intent.putExtra("src", this::class.java.name)
                    intent.putExtra("plan", weeklyPlanList[index].toString())
                    startActivity(intent)
                }
            } else {
                // Hide unused layouts and titles
                workoutLayouts[index].visibility = View.INVISIBLE
                workoutLayouts[index].isEnabled = false
                workoutTitleViews[index].visibility = View.INVISIBLE
                workoutTitleViews[index].isEnabled = false
            }
        }
    }

    private fun setMainActivityButtonListener() {
        val closeButton = findViewById<RelativeLayout>(R.id.manage_plan_close_btn)
        closeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadWeekPlan() {
        val plans = settings.getString("plans")
        val plansArray = JSONArray(plans)
        val planList = mutableListOf<JSONObject>()

        val todayDateCode = getTodayDateCode(dayOfWeekCodes)[0]

        for (i in 0 until plansArray.length()) {
            val planSettings = JSONObject(plansArray[i].toString())
            if (planSettings.getString("time") != todayDateCode) {
                planList.add(planSettings)
            }
        }

        weeklyPlanList = planList
    }
}
