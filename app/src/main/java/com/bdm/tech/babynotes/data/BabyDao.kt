package com.bdm.tech.babynotes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyDao {
    @Query("SELECT * FROM babies ORDER BY name ASC")
    fun getAllFlow(): Flow<List<Baby>>

    @Insert
    suspend fun insert(baby: Baby): Long

    @Update
    suspend fun update(baby: Baby)

    @Query("DELETE FROM babies WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM babies WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Baby?
}
