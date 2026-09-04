package com.refilliq.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val strength: String,

    val quantity: Double,

    val quantityUnit: String,

    val status: String = "ACTIVE",

    val suspensionReason: String = "",

    val suspendedAt: Long? = null
)