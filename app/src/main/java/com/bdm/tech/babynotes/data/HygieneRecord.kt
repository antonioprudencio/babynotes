package com.bdm.tech.babynotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hygiene")
data class HygieneRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val babyId: Long,
    val timestampMillis: Long,
    val note: String = ""
)
