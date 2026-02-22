package com.bdm.tech.babynotes.ui

import java.util.Calendar

enum class FilterPeriod(val label: String) {
    TODAY("Hoje"),
    WEEK("Semana"),
    MONTH("Mês"),
    ALL("Todos")
}

data class BabyConsumptionStats(
    val babyId: Long,
    val babyName: String,
    val totalVolumeMl: Int,
    val feedingCount: Int,
    val averagePerDayMl: Float?
)

object StatisticsHelper {

    fun periodRange(period: FilterPeriod): Pair<Long, Long> {
        if (period == FilterPeriod.ALL) {
            return 0L to Long.MAX_VALUE
        }
        val cal = Calendar.getInstance()
        val endOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        val start = when (period) {
            FilterPeriod.TODAY -> {
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            FilterPeriod.WEEK -> {
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.add(Calendar.DAY_OF_YEAR, -6)
                cal.timeInMillis
            }
            FilterPeriod.MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            FilterPeriod.ALL -> 0L
        }
        return start to endOfToday
    }

    fun daysInPeriod(period: FilterPeriod): Int = when (period) {
        FilterPeriod.TODAY -> 1
        FilterPeriod.WEEK -> 7
        FilterPeriod.MONTH -> Calendar.getInstance().get(Calendar.DAY_OF_MONTH).coerceAtLeast(1)
        FilterPeriod.ALL -> 1
    }
}
