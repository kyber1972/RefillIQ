package com.refilliq.app

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Medication::class,
        SuspensionHistory::class
    ],
    version = 3
)
abstract class RefillIQDatabase : RoomDatabase() {

    abstract fun medicationDao(): MedicationDao

    abstract fun suspensionHistoryDao(): SuspensionHistoryDao
}