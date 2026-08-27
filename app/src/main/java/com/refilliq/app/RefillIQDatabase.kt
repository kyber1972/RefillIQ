package com.refilliq.app

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Medication::class,
        SuspensionHistory::class,
        MedicationSchedule::class
    ],
    version = 5
)
abstract class RefillIQDatabase : RoomDatabase() {

    abstract fun medicationDao(): MedicationDao

    abstract fun suspensionHistoryDao(): SuspensionHistoryDao

    abstract fun medicationScheduleDao(): MedicationScheduleDao
}