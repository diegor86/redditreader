package com.diegor.redditreader.util.result

import android.content.Context
import com.diegor.redditreader.R
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

fun Date.formatTimeAgo(context: Context?): String? {
    val diff = Calendar.getInstance().timeInMillis - this.time

    val seconds = abs(diff) / 1000.0
    val minutes = seconds / 60.0
    val hours = minutes / 60.0
    val days = hours / 24.0
    val years = days / 365.0

    when {
        seconds < 90 -> {
            return context?.getString(R.string.time_ago_seconds)
        }
        minutes < 45 -> {
            return context?.getString(R.string.time_ago_minutes, minutes.roundToInt())
        }
        minutes < 90 -> {
            return context?.getString(R.string.time_ago_hour)
        }
        hours < 24 -> {
            return context?.getString(R.string.time_ago_hours, hours.roundToInt())
        }
        hours < 42 -> {
            return context?.getString(R.string.time_ago_day)
        }
        days < 30 -> {
            return context?.getString(R.string.time_ago_days, days.roundToInt())
        }
        days < 45 -> {
            return context?.getString(R.string.time_ago_month)
        }
        days < 365 -> {
            return context?.getString(R.string.time_ago_months, (days / 30).roundToInt())
        }
        years < 1.5 -> {
            return context?.getString(R.string.time_ago_year)
        }
        else -> {
            return context?.getString(R.string.time_ago_years, years.roundToInt())
        }
    }
}