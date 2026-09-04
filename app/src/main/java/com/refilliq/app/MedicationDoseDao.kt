package com.refilliq.app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDoseDao {

    @Insert
    suspend fun insertDose(
        dose: MedicationDose
    )

    @Query(
        """
        SELECT * FROM medication_doses
        WHERE medicationId = :medicationId
        ORDER BY takenAt DESC
        """
    )
    fun getDosesForMedication(
        medicationId: Int
    ): Flow<List<MedicationDose>>

    @Query(
        """
        SELECT * FROM medication_doses
        WHERE medicationId = :medicationId
        ORDER BY takenAt DESC
        LIMIT 1
        """
    )
    suspend fun getLastDoseForMedication(
        medicationId: Int
    ): MedicationDose?

    @Query(
        """
        SELECT COUNT(*) FROM medication_doses
        WHERE scheduleId = :scheduleId
        AND takenAt >= :startOfDay
        AND takenAt < :endOfDay
        """
    )
    suspend fun countDoseForScheduleToday(
        scheduleId: Int,
        startOfDay: Long,
        endOfDay: Long
    ): Int

    @Query(
        """
        DELETE FROM medication_doses
        WHERE medicationId = :medicationId
        """
    )
    suspend fun deleteDosesForMedication(
        medicationId: Int
    )
}