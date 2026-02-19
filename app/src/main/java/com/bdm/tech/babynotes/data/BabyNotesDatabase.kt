package com.bdm.tech.babynotes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Baby::class, FeedingRecord::class, MedicineRecord::class],
    version = 6,  // 6: MedicineRecord - volume removido (só horário, bebê, descrição)
    exportSchema = false
)
abstract class BabyNotesDatabase : RoomDatabase() {
    abstract fun babyDao(): BabyDao
    abstract fun feedingDao(): FeedingDao
    abstract fun medicineDao(): MedicineDao

    companion object {
        @Volatile
        private var INSTANCE: BabyNotesDatabase? = null

        fun getInstance(context: Context): BabyNotesDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    BabyNotesDatabase::class.java,
                    "babynotes_db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}
