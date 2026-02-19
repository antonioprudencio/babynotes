package com.bdm.tech.babynotes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedingDao {
    @Query("SELECT * FROM feedings ORDER BY timestampMillis DESC")
    fun getAllFlow(): Flow<List<FeedingRecord>>

    @Insert
    suspend fun insert(record: FeedingRecord)

    @Query("DELETE FROM feedings WHERE id = :id")
    suspend fun deleteById(id: Long)
}
