package com.example.shapeshift

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.temporal.WeekFields

/**
 * Function to get the date code for today's date and its corresponding week index.
 * @param dateCodes List of date codes.
 * @return A list containing today's date code and its week index.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun getTodayDateCode(dateCodes: List<String>): List<String> {
    // Get today's date
    val todayDate = LocalDate.now()

    // Get the day of the week index (0 for Monday, 1 for Tuesday, ..., 6 for Sunday)
    var weekDayIndex = todayDate.get(WeekFields.ISO.dayOfWeek()) - 1

    // Adjust Sunday's index to 0
    if (weekDayIndex == 6)
        weekDayIndex = 0
    else
        weekDayIndex++

    // Return the date code for today and its week index
    return listOf(dateCodes[weekDayIndex], weekDayIndex.toString())
}
