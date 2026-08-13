package com.refilliq.app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    @Insert
    suspend fun insertMedication(
        medication: Medication
    )

    @Query("SELECT * FROM medications")
    fun getAllMedications(): Flow<List<Medication>>

    @Query(
        """
        UPDATE medications
        SET status = 'SUSPENDED',
            suspensionReason = :reason,
            suspendedAt = :suspendedAt
        WHERE id = :medicationId
        """
    )
    suspend fun suspendMedication(
        medicationId: Int,
        reason: String,
        suspendedAt: Long
    )

    @Insert
    suspend fun insertSuspensionHistory(
        suspensionHistory: SuspensionHistory
    )

    @Transaction
    suspend fun suspendMedicationWithHistory(
        medicationId: Int,
        reason: String,
        suspendedAt: Long
    ) {

        suspendMedication(
            medicationId = medicationId,
            reason = reason,
            suspendedAt = suspendedAt
        )

        insertSuspensionHistory(
            SuspensionHistory(
                medicationId = medicationId,
                reason = reason,
                suspendedAt = suspendedAt
            )
        )
    }
}