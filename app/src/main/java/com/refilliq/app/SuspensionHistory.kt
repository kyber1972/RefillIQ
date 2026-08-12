package com.refilliq.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suspension_history")
data class SuspensionHistory(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val medicationId: Int,

    val reason: String,

    val suspendedAt: Long,

    val resumedAt: Long? = null
)