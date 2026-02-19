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
     * Horário é atribuído aqui (timestampMillis) no momento do insert.
     * babyId, feedingType e volume vêm do diálogo.
     */
    suspend fun addFeeding(babyId: Long, feedingType: FeedingType, volume: Int = 0) {
        feedingDao.insert(
            FeedingRecord(
                babyId = babyId,
                timestampMillis = System.currentTimeMillis(),
                feedingType = feedingType,
                volume = volume
            )
        )
    }

    /**
     * Horário é atribuído aqui (timestampMillis) no momento do insert.
     * babyId e note (descrição) vêm do diálogo.
     */
    suspend fun addMedicine(babyId: Long, note: String = "") {
        medicineDao.insert(
            MedicineRecord(
                babyId = babyId,
                timestampMillis = System.currentTimeMillis(),
                note = note
            )
        )
    }

    suspend fun deleteFeeding(id: Long) = feedingDao.deleteById(id)
    suspend fun deleteMedicine(id: Long) = medicineDao.deleteById(id)
}
