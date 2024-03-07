package com.example.shapeshift

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.shapeshift.databases.DatabaseManager
import com.example.shapeshift.databases.Setting
import org.json.JSONObject

class WorkoutActivity : AppCompatActivity() {
    lateinit var source: String
    lateinit var workout_plan: JSONObject
    lateinit var workout_struct: MutableList<JSONObject>
    private var isPaused: Boolean = false

    private lateinit var settings: JSONObject

    // STOP WATCH VALUE
    lateinit var stopwatchTextView: TextView
    private var secondsElapsed: Int = 0
    private var isRunning: Boolean = false
    private val handler = Handler()

    private val availableThemes: List<String> = listOf("dark", "light", "default")


    // WORKOUT CURSOR
    var workout_cursor: Int = 0

    val workout_squares = mutableListOf<FrameLayout>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        this.load_settings()

        stopwatchTextView = findViewById(R.id.workout_stopwatch)

        this.INIT_EXTRA_VALUE()
        this.ADD_TITLE()
        this.SETUP_WORKOUT_STRUCT()

        this.SET_THEME_TO_ACTIVITY()

        // EVENT
        this.SET_EXIT_EVENT()
        this.SET_PAUSE_EVENT()
        this.START_WORKOUT_IDX()
        this.startStopwatch()

        this.SET_WORKOUT_CURSOR_EVENT()
    }

    private fun load_settings() {
        val dbManager = DatabaseManager(this)
        val setting: Setting? = dbManager.getSpecificSetting("json")
        if (setting != null) {
            settings = JSONObject(setting.value)
        }
    }

    // STOP WATCH LOOP
    private val runnable = object : Runnable {
        override fun run() {
            secondsElapsed--

            if (secondsElapsed >= 0) {
                val minutes = (secondsElapsed % 3600) / 60
                val seconds = secondsElapsed % 60
                var text = "$minutes:"

                text += if (seconds < 10) {
                    "0$seconds"
                } else {
                    "$seconds"
                }
                stopwatchTextView.text = text
            }

            handler.postDelayed(this, 1000)

        }
    }

    private fun SET_THEME_TO_ACTIVITY() {
        val page = findViewById<ConstraintLayout>(R.id.page)
        val title = findViewById<TextView>(R.id.workout_exo_name)

        if (availableThemes[1] == settings.getString("theme")) {
            page.setBackgroundColor(getColor(R.color.white))
            title.setTextColor(getColor(R.color.light_gray))

            for (square in workout_squares) {
                square.setBackgroundColor(getColor(R.color.light))
            }
        }

    }

    private fun vibrate(time:Long=50) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(time)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopStopwatch()
    }

    private fun startStopwatch() {
        if (!isRunning) {
            isRunning = true
            handler.post(runnable)
        }
    }

    private fun stopStopwatch() {
        if (isRunning) {
            isRunning = false
            handler.removeCallbacks(runnable)
        }
    }

    private fun UPDATE_SQUARE_BAR() {
        val square_cursor: Int = if (workout_cursor % 2 == 1) {
            (workout_cursor - 1) / 2
        } else {
            workout_cursor / 2
        }

        for (sq in 0 until workout_squares.size) {
            val squareFrame = workout_squares[sq]

            if (square_cursor == sq) {
                squareFrame.setBackgroundColor(getColor(R.color.orange))
            } else {
                if (availableThemes[1] == settings.getString("theme")) {
                    squareFrame.setBackgroundColor(getColor(R.color.light))
                } else {
                    squareFrame.setBackgroundColor(getColor(R.color.light_gray))
                }
            }
        }
    }

    private fun SET_WORKOUT_CURSOR_EVENT() {
        val rightCursor = findViewById<FrameLayout>(R.id.frameLayout2)
        val leftCursor = findViewById<FrameLayout>(R.id.frameLayout)
        val pauseBtn = findViewById<FrameLayout>(R.id.frameLayout3)

        rightCursor.setOnClickListener {
            this.vibrate()
            if (workout_cursor+1 < workout_struct.size) {
                workout_cursor++
                this.START_WORKOUT_IDX()
                this.UPDATE_SQUARE_BAR()
                pauseBtn.setBackgroundResource(R.mipmap.ic_pause)
            }
        }

        leftCursor.setOnClickListener {
            this.vibrate()
            if (workout_cursor-1 >= 0) {
                workout_cursor--
                this.START_WORKOUT_IDX()
                this.UPDATE_SQUARE_BAR()
                pauseBtn.setBackgroundResource(R.mipmap.ic_pause)
            }
        }
    }

    private fun START_WORKOUT_IDX() {
        secondsElapsed = 0

        val textView = findViewById<TextView>(R.id.workout_exo_name)

        val exercise = workout_struct[workout_cursor]

        val exerciseName = exercise.getString("name")

        val text: String

        if (exerciseName == "BREAK") {
            secondsElapsed = exercise.getInt("time")
            startStopwatch()
            text = exerciseName
        }

        else {
            secondsElapsed = 1
            startStopwatch()
            text = "${exercise.getString("rep")} x ${exerciseName.uppercase()}"
        }

        textView.text = text
    }

    private fun SETUP_WORKOUT_STRUCT() {
        val steps = mutableListOf<JSONObject>()
        val workoutList = workout_plan.getJSONArray("workout")
        val squares_bar = findViewById<RelativeLayout>(R.id.frameLayout5)
        var squareX = 0
        var squareY = 0

        for (stepIDX in 0 until workoutList.length()) {
            val exercise = JSONObject(workoutList[stepIDX].toString())

            for (series in 0 until exercise.getString("series").toInt()) {
                val breaks = JSONObject()
                breaks.put("name", "BREAK")
                breaks.put("time", exercise.getString("break").toInt())

                steps.add(exercise)
                steps.add(breaks)

                val oneSquare = FrameLayout(this)
                val layoutParams = ViewGroup.MarginLayoutParams(45, 45)


                if (squareX >= 15) {
                    squareX = 0
                    squareY++
                }

                layoutParams.marginStart = 60 * squareX
                layoutParams.topMargin = 60 * squareY
                oneSquare.layoutParams = layoutParams
                oneSquare.setBackgroundColor(getColor(R.color.light_gray))

                if (squareX == 0 && squareY == 0) {
                    oneSquare.setBackgroundColor(getColor(R.color.orange))
                }

                squares_bar.addView(oneSquare)
                workout_squares.add(oneSquare)
                squareX++
           }
        }

        workout_struct = steps
    }

    private fun INIT_EXTRA_VALUE() {
        val callerActivity = intent.getStringExtra("src")
        val callerPlan = intent.getStringExtra("plan")

        source = callerActivity.toString()
        workout_plan = JSONObject(callerPlan.toString())
    }

    private fun ADD_TITLE() {
        val textView = findViewById<TextView>(R.id.workout_title)
        val title = workout_plan.getString("name")
        textView.text = title

    }

    private fun SET_EXIT_EVENT() {
        val exit_button = findViewById<FrameLayout>(R.id.workout_exit_button)
        exit_button.setOnClickListener {
            val intent = Intent(this, Class.forName(source))
            startActivity(intent)
        }
    }

    private fun SET_PAUSE_EVENT() {
        val pause_button = findViewById<FrameLayout>(R.id.frameLayout3)
        pause_button.setOnClickListener {
            if (isPaused) {
                startStopwatch()
                pause_button.setBackgroundResource(R.mipmap.ic_pause)
            } else {
                stopStopwatch()
                pause_button.setBackgroundResource(R.mipmap.ic_stop)
            }

            isPaused = !isPaused
        }
    }
}