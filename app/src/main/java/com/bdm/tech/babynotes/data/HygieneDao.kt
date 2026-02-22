package com.bdm.tech.babynotes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HygieneDao {
    @Query("SELECT * FROM hygiene ORDER BY timestampMillis DESC")
    fun getAllFlow(): Flow<List<HygieneRecord>>

    @Insert
    suspend fun insert(record: HygieneRecord)

    @Query("DELETE FROM hygiene WHERE id = :id")
    suspend fun deleteById(id: Long)
}
