package com.refilliq.app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SuspensionHistoryDao {

    @Insert
    suspend fun insertSuspension(
        suspension: SuspensionHistory
    )

    @Query(
        """
        SELECT * FROM suspension_history
        WHERE medicationId = :medicationId
        ORDER BY suspendedAt DESC
        """
    )
    suspend fun getSuspensionHistory(
        medicationId: Int
    ): List<SuspensionHistory>

    @Query(
        """
        UPDATE suspension_history
        SET resumedAt = :resumedAt
        WHERE id = :suspensionId
        """
    )
    suspend fun resumeSuspension(
        suspensionId: Int,
        resumedAt: Long
    )
}