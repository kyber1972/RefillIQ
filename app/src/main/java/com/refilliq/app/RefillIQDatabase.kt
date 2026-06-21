package com.refilliq.app

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Medication::class],
    version = 1
)
abstract class RefillIQDatabase : RoomDatabase() {

    abstract fun medicationDao(): MedicationDao
}