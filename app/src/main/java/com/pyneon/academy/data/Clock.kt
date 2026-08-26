package com.pyneon.academy.data

import java.util.Calendar

object Clock {

    fun todayEpochDay(): Long {
        val c = Calendar.getInstance()
        return daysFromCivil(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
    }

    fun currentHour(): Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    private fun daysFromCivil(year: Int, month: Int, day: Int): Long {
        val y = if (month <= 2) year - 1 else year
        val m = month
        val era = Math.floorDiv(y.toLong(), 400L)
        val yoe = y - era * 400
        val mp = (m + 9) % 12
        val doy = (153L * mp + 2L) / 5L + day - 1
        val doe = yoe * 365L + Math.floorDiv(yoe, 4L) - Math.floorDiv(yoe, 100L) + doy
        return era * 146097L + doe - 719468L
    }
}
