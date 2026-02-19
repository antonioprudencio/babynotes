package com.bdm.tech.babynotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedings")
data class FeedingRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val babyId: Long,
    val timestampMillis: Long,
    val feedingType: FeedingType = FeedingType.MAMA,
    val volume: Int = 0
)
