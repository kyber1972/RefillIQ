package com.refilliq.app

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow

class MedicationRepository(
    private val database: RefillIQDatabase,
    private val medicationDao: MedicationDao,
    private val suspensionHistoryDao: SuspensionHistoryDao,
    private val medicationScheduleDao: MedicationScheduleDao,
    private val medicationDoseDao: MedicationDoseDao,
    private val medicationInventoryMovementDao: MedicationInventoryMovementDao
) {

    suspend fun insertMedication(
        medication: Medication
    ) {
        database.withTransaction {

            val medicationId =
                medicationDao.insertMedication(
                    medication
                )

            if (medication.quantity > 0.0) {

                medicationInventoryMovementDao.insertMovement(
                    MedicationInventoryMovement(
                        medicationId = medicationId.toInt(),
                        type = "OPENING_BALANCE",
                        quantity = medication.quantity,
                        reason = "Initial inventory",
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        }
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

    suspend fun decreaseMedicationQuantity(
        medicationId: Int,
        dose: Double
    ): Int {
        return medicationDao.decreaseMedicationQuantity(
            medicationId = medicationId,
            dose = dose
        )
    }

    suspend fun increaseMedicationQuantity(
        medicationId: Int,
        dose: Double
    ) {
        medicationDao.increaseMedicationQuantity(
            medicationId = medicationId,
            dose = dose
        )
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

    suspend fun insertInventoryMovement(
        movement: MedicationInventoryMovement
    ) {
        medicationInventoryMovementDao.insertMovement(
            movement
        )
    }

    fun getInventoryMovementsForMedication(
        medicationId: Int
    ): Flow<List<MedicationInventoryMovement>> {
        return medicationInventoryMovementDao.getMovementsForMedication(
            medicationId
        )
    }

    suspend fun getInventoryMovementsForMedicationOnce(
        medicationId: Int
    ): List<MedicationInventoryMovement> {
        return medicationInventoryMovementDao.getMovementsForMedicationOnce(
            medicationId
        )
    }

    suspend fun recordTakenDose(
        medicationId: Int,
        scheduleId: Int?,
        dose: Double,
        doseUnit: String,
        takenAt: Long
    ): Boolean = database.withTransaction {

        if (dose <= 0.0) {
            return@withTransaction false
        }

        val changed = medicationDao.decreaseMedicationQuantity(
            medicationId = medicationId,
            dose = dose
        )

        if (changed == 0) {
            return@withTransaction false
        }

        medicationInventoryMovementDao.insertMovement(
            MedicationInventoryMovement(
                medicationId = medicationId,
                type = "OUT",
                quantity = dose,
                reason = "Medication taken",
                createdAt = takenAt
            )
        )

        medicationDoseDao.insertDose(
            MedicationDose(
                medicationId = medicationId,
                scheduleId = scheduleId,
                dose = dose,
                doseUnit = doseUnit,
                takenAt = takenAt
            )
        )

        true
    }

    suspend fun recordInventoryIn(
        medicationId: Int,
        quantity: Double,
        reason: String,
        createdAt: Long
    ): Boolean = database.withTransaction {

        if (quantity <= 0.0) {
            return@withTransaction false
        }

        if (reason.isBlank()) {
            return@withTransaction false
        }

        val changed = medicationDao.increaseMedicationQuantity(
            medicationId = medicationId,
            dose = quantity
        )

        if (changed == 0) {
            return@withTransaction false
        }

        medicationInventoryMovementDao.insertMovement(
            MedicationInventoryMovement(
                medicationId = medicationId,
                type = "IN",
                quantity = quantity,
                reason = reason,
                createdAt = createdAt
            )
        )

        true
    }

    suspend fun recordInventoryAdjustment(
        medicationId: Int,
        adjustment: Double,
        reason: String,
        createdAt: Long
    ): Boolean = database.withTransaction {

        if (adjustment == 0.0) {
            return@withTransaction false
        }

        if (reason.isBlank()) {
            return@withTransaction false
        }

        val changed =
            if (adjustment > 0.0) {

                medicationDao.increaseMedicationQuantity(
                    medicationId = medicationId,
                    dose = adjustment
                )

            } else {

                medicationDao.decreaseMedicationQuantity(
                    medicationId = medicationId,
                    dose = -adjustment
                )
            }

        if (changed == 0) {
            return@withTransaction false
        }

        medicationInventoryMovementDao.insertMovement(
            MedicationInventoryMovement(
                medicationId = medicationId,
                type = "ADJUSTMENT",
                quantity = adjustment,
                reason = reason,
                createdAt = createdAt
            )
        )

        true
    }
}
