package com.refilliq.app

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Medication::class,
        SuspensionHistory::class,
        MedicationSchedule::class,
        MedicationDose::class,
        MedicationInventoryMovement::class
    ],
    version = 10
)
abstract class RefillIQDatabase : RoomDatabase() {

    abstract fun medicationDao(): MedicationDao

    abstract fun suspensionHistoryDao(): SuspensionHistoryDao

    abstract fun medicationScheduleDao(): MedicationScheduleDao

    abstract fun medicationDoseDao(): MedicationDoseDao

    abstract fun medicationInventoryMovementDao(): MedicationInventoryMovementDao
}