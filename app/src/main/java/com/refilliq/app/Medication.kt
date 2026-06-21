package com.refilliq.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val strength: String,

    val quantity: String,

    val dailyUsage: String
)