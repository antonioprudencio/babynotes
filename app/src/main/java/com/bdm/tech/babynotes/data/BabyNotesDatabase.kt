package com.bdm.tech.babynotes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Baby::class, FeedingRecord::class, MedicineRecord::class, HygieneRecord::class],
    version = 7,  // 7: HygieneRecord (aba Higiene)
    exportSchema = false
)
abstract class BabyNotesDatabase : RoomDatabase() {
    abstract fun babyDao(): BabyDao
    abstract fun feedingDao(): FeedingDao
    abstract fun medicineDao(): MedicineDao
    abstract fun hygieneDao(): HygieneDao

    companion object {
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS hygiene (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        babyId INTEGER NOT NULL,
                        timestampMillis INTEGER NOT NULL,
                        note TEXT NOT NULL DEFAULT ''
                    )
                    """.trimIndent()
                )
            }
        }

        @Volatile
        private var INSTANCE: BabyNotesDatabase? = null

        fun getInstance(context: Context): BabyNotesDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    BabyNotesDatabase::class.java,
                    "babynotes_db"
                )
                    .addMigrations(MIGRATION_6_7)
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}
