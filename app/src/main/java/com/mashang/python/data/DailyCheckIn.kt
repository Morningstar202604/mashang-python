package com.mashang.python.data

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class DailyCheckIn(
    val date: String,
    var isCheckedIn: Boolean = false,
    var xpEarned: Int = 0
) : Serializable

object CheckInManager {
    
    fun getTodayKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
    
    fun isCheckedInToday(user: User): Boolean {
        val today = getTodayKey()
        return user.dailyCheckIn[today] == true
    }
    
    fun checkIn(user: User): Int {
        val today = getTodayKey()
        
        if (isCheckedInToday(user)) {
            return 0 // Already checked in today
        }
        
        val yesterday = getYesterdayKey()
        val hasYesterday = user.dailyCheckIn[yesterday] == true
        
        // Calculate streak
        if (hasYesterday) {
            // Continuing streak
            user.streak++
        } else {
            // Missed yesterday (or this is first check-in)
            // Check if user has any check-in history
            if (user.dailyCheckIn.isEmpty()) {
                user.streak = 1
            } else {
                // Find last check-in date
                val lastCheckIn = user.dailyCheckIn.keys.sorted().lastOrNull()
                if (lastCheckIn == yesterday) {
                    user.streak++
                } else {
                    // Gap in check-ins, reset streak
                    user.streak = 1
                }
            }
        }
        
        // Cap streak at 365 days
        if (user.streak > 365) user.streak = 365
        
        // Mark as checked in
        user.dailyCheckIn[today] = true
        
        // Calculate XP reward (base 10 + streak bonus, max 50 per day)
        val xp = minOf(10 + (user.streak * 2), 50)
        user.addXp(xp)
        
        return xp
    }
    
    private fun getYesterdayKey(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(cal.time)
    }
    
    fun getStreakDays(user: User): Int {
        return user.streak
    }
    
    fun getCheckInHistory(user: User, days: Int): List<DailyCheckIn> {
        val history = mutableListOf<DailyCheckIn>()
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        for (i in 0 until days) {
            val date = sdf.format(cal.time)
            val isCheckedIn = user.dailyCheckIn[date] == true
            history.add(DailyCheckIn(date, isCheckedIn))
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        
        return history
    }
}
