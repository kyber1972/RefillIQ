package com.refilliq.app

class MedicationRepository(
    private val medicationDao: MedicationDao
) {

    suspend fun insertMedication(
        medication: Medication
    ) {
        medicationDao.insertMedication(medication)
    }

    suspend fun getAllMedications(): List<Medication> {
        return medicationDao.getAllMedications()
    }
}