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

    @Query(
        """
        UPDATE medications
        SET name = :name,
            strength = :strength,
            quantity = :quantity,
            quantityUnit = :quantityUnit
        WHERE id = :medicationId
        """
    )
    suspend fun updateMedication(
        medicationId: Int,
        name: String,
        strength: String,
        quantity: Double,
        quantityUnit: String
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

    @Query(
        """
        UPDATE medications
        SET status = 'ACTIVE',
            suspensionReason = '',
            suspendedAt = NULL
        WHERE id = :medicationId
        """
    )
    suspend fun resumeMedication(
        medicationId: Int
    )

    @Insert
    suspend fun insertSuspensionHistory(
        suspensionHistory: SuspensionHistory
    )

    @Query(
        """
        UPDATE suspension_history
        SET resumedAt = :resumedAt
        WHERE id = :suspensionId
        """
    )
    suspend fun resumeSuspensionHistory(
        suspensionId: Int,
        resumedAt: Long
    )

    @Query(
        """
        DELETE FROM medications
        WHERE id = :medicationId
        """
    )
    suspend fun deleteMedication(
        medicationId: Int
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

    @Transaction
    suspend fun resumeMedicationWithHistory(
        medicationId: Int,
        suspensionId: Int,
        resumedAt: Long
    ) {

        resumeMedication(
            medicationId = medicationId
        )

        resumeSuspensionHistory(
            suspensionId = suspensionId,
            resumedAt = resumedAt
        )
    }
}