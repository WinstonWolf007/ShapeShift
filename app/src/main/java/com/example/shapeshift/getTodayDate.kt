package com.example.shapeshift

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.temporal.WeekFields


@RequiresApi(Build.VERSION_CODES.O)
fun GET_TODAY_DATE_CODE(dateCode: List<String>): List<String> {
    val today = LocalDate.now()
    var weekIDX = today.get(WeekFields.ISO.dayOfWeek())-1

    if (weekIDX == 6)
        weekIDX = 0
    else
        weekIDX++

    return listOf(dateCode[weekIDX], weekIDX.toString())
}
