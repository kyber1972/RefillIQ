package com.refilliq.app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationScheduleDao {

    @Insert
    suspend fun insertSchedule(
        schedule: MedicationSchedule
    )

    @Update
    suspend fun updateSchedule(
        schedule: MedicationSchedule
    )

    @Query(
        """
        SELECT * FROM medication_schedules
        WHERE medicationId = :medicationId
        ORDER BY time ASC
        """
    )
    fun getSchedulesForMedication(
        medicationId: Int
    ): Flow<List<MedicationSchedule>>

    @Query(
        """
        SELECT COUNT(*) FROM medication_schedules
        WHERE medicationId = :medicationId
        AND time = :time
        """
    )
    suspend fun countScheduleAtTime(
        medicationId: Int,
        time: String
    ): Int

    @Query(
        """
        DELETE FROM medication_schedules
        WHERE id = :scheduleId
        """
    )
    suspend fun deleteSchedule(
        scheduleId: Int
    )

    @Query(
        """
        DELETE FROM medication_schedules
        WHERE medicationId = :medicationId
        """
    )
    suspend fun deleteSchedulesForMedication(
        medicationId: Int
    )
}