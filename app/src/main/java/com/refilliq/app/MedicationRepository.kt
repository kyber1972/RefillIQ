package com.refilliq.app

import kotlinx.coroutines.flow.Flow

class MedicationRepository(
    private val medicationDao: MedicationDao,
    private val suspensionHistoryDao: SuspensionHistoryDao
) {

    suspend fun insertMedication(
        medication: Medication
    ) {
        medicationDao.insertMedication(medication)
    }

    fun getAllMedications(): Flow<List<Medication>> {
        return medicationDao.getAllMedications()
    }

    suspend fun insertSuspension(
        suspension: SuspensionHistory
    ) {
        suspensionHistoryDao.insertSuspension(suspension)
    }

    suspend fun getSuspensionHistory(
        medicationId: Int
    ): List<SuspensionHistory> {
        return suspensionHistoryDao.getSuspensionHistory(medicationId)
    }
}