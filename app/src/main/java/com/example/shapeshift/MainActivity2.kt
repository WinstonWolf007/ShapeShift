package com.example.shapeshift

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.shapeshift.databinding.ActivityMain2Binding
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.temporal.WeekFields

import com.example.shapeshift.GET_TODAY_DATE_CODE

class MainActivity2 : AppCompatActivity() {

    private lateinit var listOfPlansInWeek: MutableList<JSONObject>
    private val dateCode = listOf<String>("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        this.ADD_WEEK_PLAN()
        this.ADD_WORKOUT_LAYOUT()

        // Event
        this.CHANGE_TO_MAIN_ACTIVITY()

    }

    private fun ADD_WORKOUT_LAYOUT() {
        val maxWorkout = 6

        val workoutTitles = listOf<TextView>(
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


        for (IDX in 0 until maxWorkout) {
            if (IDX < listOfPlansInWeek.size) {
                workoutLayouts[IDX].visibility = View.VISIBLE
                workoutLayouts[IDX].isEnabled = true

                workoutTitles[IDX].visibility = View.VISIBLE
                workoutTitles[IDX].isEnabled = true

                workoutTitles[IDX].text = listOfPlansInWeek[IDX].getString("name")

                workoutLayouts[IDX].setOnClickListener {
                    val intent = Intent(this, Workout::class.java)
                    intent.putExtra("src", this::class.java.name)
                    intent.putExtra("plan", listOfPlansInWeek[IDX].toString())
                    startActivity(intent)
                }
            } else {
                workoutLayouts[IDX].visibility = View.INVISIBLE
                workoutLayouts[IDX].isEnabled = false
                workoutTitles[IDX].visibility = View.INVISIBLE
                workoutTitles[IDX].isEnabled = false
            }
        }
    }

    private fun CHANGE_TO_MAIN_ACTIVITY() {
        val button = findViewById<RelativeLayout>(R.id.manage_plan_close_btn)
        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ADD_WEEK_PLAN() {
        val jsonObject: JSONObject? = GET_JSON_FILE_OBJECT_INTERN_STORAGE(application, "default-settings.json")

        if (jsonObject != null) {
            val plans = jsonObject.getString("plans")
            val plans_array = JSONArray(plans)
            val listOfPlans = mutableListOf<JSONObject>()

            for (i in 0 until plans_array.length()) {
                val planSettings = JSONObject(plans_array[i].toString())
                if (planSettings.getString("time") != GET_TODAY_DATE_CODE(dateCode)[0]) {
                    listOfPlans.add(planSettings)
                }
            }

            listOfPlansInWeek = listOfPlans
        }
    }
}