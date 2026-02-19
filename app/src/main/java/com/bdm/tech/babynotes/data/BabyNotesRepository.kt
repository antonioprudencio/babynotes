package com.bdm.tech.babynotes.data

import kotlinx.coroutines.flow.Flow

class BabyNotesRepository(
    private val babyDao: BabyDao,
    private val feedingDao: FeedingDao,
    private val medicineDao: MedicineDao
) {
    val allBabies: Flow<List<Baby>> = babyDao.getAllFlow()
    val allFeedings: Flow<List<FeedingRecord>> = feedingDao.getAllFlow()
    val allMedicine: Flow<List<MedicineRecord>> = medicineDao.getAllFlow()

    suspend fun addBaby(name: String) {
        babyDao.insert(Baby(name = name.trim()))
    }

    suspend fun deleteBaby(id: Long) = babyDao.deleteById(id)

    /**
     * babyId, feedingType, volume e timestampMillis vêm do diálogo (timestamp editável).
     */
    suspend fun addFeeding(babyId: Long, feedingType: FeedingType, volume: Int = 0, timestampMillis: Long = System.currentTimeMillis()) {
        feedingDao.insert(
            FeedingRecord(
                babyId = babyId,
                timestampMillis = timestampMillis,
                feedingType = feedingType,
                volume = volume
            )
        )
    }

    /**
     * babyId, note e timestampMillis vêm do diálogo (timestamp editável).
     */
    suspend fun addMedicine(babyId: Long, note: String = "", timestampMillis: Long = System.currentTimeMillis()) {
        medicineDao.insert(
            MedicineRecord(
                babyId = babyId,
                timestampMillis = timestampMillis,
                note = note
            )
        )
    }

    suspend fun deleteFeeding(id: Long) = feedingDao.deleteById(id)
    suspend fun deleteMedicine(id: Long) = medicineDao.deleteById(id)
}
