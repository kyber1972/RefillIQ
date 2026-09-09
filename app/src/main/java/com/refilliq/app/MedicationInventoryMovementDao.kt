package com.refilliq.app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationInventoryMovementDao {

    @Insert
    suspend fun insertMovement(
        movement: MedicationInventoryMovement
    )

    @Query(
        """
        SELECT * FROM medication_inventory_movements
        WHERE medicationId = :medicationId
        ORDER BY createdAt DESC
        """
    )
    fun getMovementsForMedication(
        medicationId: Int
    ): Flow<List<MedicationInventoryMovement>>

    @Query(
        """
        SELECT * FROM medication_inventory_movements
        WHERE medicationId = :medicationId
        ORDER BY createdAt DESC
        """
    )
    suspend fun getMovementsForMedicationOnce(
        medicationId: Int
    ): List<MedicationInventoryMovement>
}