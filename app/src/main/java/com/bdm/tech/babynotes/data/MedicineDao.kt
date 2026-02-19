package com.bdm.tech.babynotes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicine ORDER BY timestampMillis DESC")
    fun getAllFlow(): Flow<List<MedicineRecord>>

    @Insert
    suspend fun insert(record: MedicineRecord)

    @Query("DELETE FROM medicine WHERE id = :id")
    suspend fun deleteById(id: Long)
}
