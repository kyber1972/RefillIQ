package com.refilliq.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_schedules")
data class MedicationSchedule(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val medicationId: Int,

    val dose: String,

    val time: String
)