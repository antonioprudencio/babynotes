package com.bdm.tech.babynotes.data

import kotlinx.coroutines.flow.Flow

class BabyNotesRepository(
    private val babyDao: BabyDao,
    private val feedingDao: FeedingDao,
    private val medicineDao: MedicineDao,
    private val hygieneDao: HygieneDao
) {
    val allBabies: Flow<List<Baby>> = babyDao.getAllFlow()
    val allFeedings: Flow<List<FeedingRecord>> = feedingDao.getAllFlow()
    val allMedicine: Flow<List<MedicineRecord>> = medicineDao.getAllFlow()
    val allHygiene: Flow<List<HygieneRecord>> = hygieneDao.getAllFlow()

    suspend fun addBaby(name: String) {
        babyDao.insert(Baby(name = name.trim()))
    }

    suspend fun updateBaby(baby: Baby) = babyDao.update(baby)
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

    suspend fun addHygiene(babyId: Long, note: String = "", timestampMillis: Long = System.currentTimeMillis()) {
        hygieneDao.insert(
            HygieneRecord(
                babyId = babyId,
                timestampMillis = timestampMillis,
                note = note
            )
        )
    }

    suspend fun updateFeeding(record: FeedingRecord) = feedingDao.update(record)
    suspend fun updateMedicine(record: MedicineRecord) = medicineDao.update(record)
    suspend fun updateHygiene(record: HygieneRecord) = hygieneDao.update(record)

    suspend fun deleteFeeding(id: Long) = feedingDao.deleteById(id)
    suspend fun deleteMedicine(id: Long) = medicineDao.deleteById(id)
    suspend fun deleteHygiene(id: Long) = hygieneDao.deleteById(id)
}
