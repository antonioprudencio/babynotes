package com.bdm.tech.babynotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "babies")
data class Baby(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
)
