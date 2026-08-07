package com.refilliq.app

import kotlinx.coroutines.flow.Flow

class MedicationRepository(
    private val medicationDao: MedicationDao
) {

    suspend fun insertMedication(
        medication: Medication
    ) {
        medicationDao.insertMedication(medication)
    }

    fun getAllMedications(): Flow<List<Medication>> {
        return medicationDao.getAllMedications()
    }
}