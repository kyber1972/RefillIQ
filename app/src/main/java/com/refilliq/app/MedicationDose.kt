package com.refilliq.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_doses")
data class MedicationDose(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val medicationId: Int,

    val scheduleId: Int? = null,

    val dose: Double,

    val doseUnit: String,

    val takenAt: Long
)