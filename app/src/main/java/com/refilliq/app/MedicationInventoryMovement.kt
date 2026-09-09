package com.refilliq.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_inventory_movements")
data class MedicationInventoryMovement(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val medicationId: Int,

    val type: String,

    val quantity: Double,

    val createdAt: Long
)