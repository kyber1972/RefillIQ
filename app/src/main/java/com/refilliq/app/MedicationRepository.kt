package com.refilliq.app

import kotlinx.coroutines.flow.Flow

class MedicationRepository(
    private val medicationDao: MedicationDao,
    private val suspensionHistoryDao: SuspensionHistoryDao,
    private val medicationScheduleDao: MedicationScheduleDao,
    private val medicationDoseDao: MedicationDoseDao
) {

    suspend fun insertMedication(
        medication: Medication
    ) {
        medicationDao.insertMedication(
            medication
        )
    }

    suspend fun updateMedication(
        medicationId: Int,
        name: String,
        strength: String,
        quantity: Double,
        quantityUnit: String
    ) {
        medicationDao.updateMedication(
            medicationId = medicationId,
            name = name,
            strength = strength,
            quantity = quantity,
            quantityUnit = quantityUnit
        )
    }

    fun getAllMedications(): Flow<List<Medication>> {
        return medicationDao.getAllMedications()
    }

    suspend fun suspendMedication(
        medicationId: Int,
        reason: String,
        suspendedAt: Long
    ) {
        medicationDao.suspendMedicationWithHistory(
            medicationId = medicationId,
            reason = reason,
            suspendedAt = suspendedAt
        )
    }

    suspend fun resumeMedication(
        medicationId: Int,
        suspensionId: Int,
        resumedAt: Long
    ) {
        medicationDao.resumeMedicationWithHistory(
            medicationId = medicationId,
            suspensionId = suspensionId,
            resumedAt = resumedAt
        )
    }

    suspend fun insertSuspension(
        suspension: SuspensionHistory
    ) {
        suspensionHistoryDao.insertSuspension(
            suspension
        )
    }

    suspend fun getSuspensionHistory(
        medicationId: Int
    ): List<SuspensionHistory> {
        return suspensionHistoryDao.getSuspensionHistory(
            medicationId
        )
    }

    suspend fun insertSchedule(
        schedule: MedicationSchedule
    ) {
        medicationScheduleDao.insertSchedule(
            schedule
        )
    }

    suspend fun updateSchedule(
        schedule: MedicationSchedule
    ) {
        medicationScheduleDao.updateSchedule(
            schedule
        )
    }

    suspend fun hasScheduleAtTime(
        medicationId: Int,
        time: String
    ): Boolean {
        return medicationScheduleDao.countScheduleAtTime(
            medicationId = medicationId,
            time = time
        ) > 0
    }

    suspend fun deleteSchedule(
        scheduleId: Int
    ) {
        medicationScheduleDao.deleteSchedule(
            scheduleId = scheduleId
        )
    }

    fun getSchedulesForMedication(
        medicationId: Int
    ): Flow<List<MedicationSchedule>> {
        return medicationScheduleDao.getSchedulesForMedication(
            medicationId
        )
    }

    suspend fun deleteSchedulesForMedication(
        medicationId: Int
    ) {
        medicationScheduleDao.deleteSchedulesForMedication(
            medicationId
        )
    }

    suspend fun insertDose(
        dose: MedicationDose
    ) {
        medicationDoseDao.insertDose(
            dose
        )
    }

    fun getDosesForMedication(
        medicationId: Int
    ): Flow<List<MedicationDose>> {
        return medicationDoseDao.getDosesForMedication(
            medicationId
        )
    }

    suspend fun getLastDoseForMedication(
        medicationId: Int
    ): MedicationDose? {
        return medicationDoseDao.getLastDoseForMedication(
            medicationId
        )
    }

    suspend fun deleteDosesForMedication(
        medicationId: Int
    ) {
        medicationDoseDao.deleteDosesForMedication(
            medicationId
        )
    }

    suspend fun countDoseForScheduleToday(
        scheduleId: Int,
        startOfDay: Long,
        endOfDay: Long
    ): Int {
        return medicationDoseDao.countDoseForScheduleToday(
            scheduleId = scheduleId,
            startOfDay = startOfDay,
            endOfDay = endOfDay
        )
    }
}