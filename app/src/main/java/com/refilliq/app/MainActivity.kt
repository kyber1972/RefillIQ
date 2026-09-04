package com.refilliq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.refilliq.app.ui.theme.RefillIQTheme


val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(
        db: SupportSQLiteDatabase
    ) {
        db.execSQL(
            """
            ALTER TABLE medications
            ADD COLUMN status TEXT NOT NULL DEFAULT 'ACTIVE'
            """.trimIndent()
        )

        db.execSQL(
            """
            ALTER TABLE medications
            ADD COLUMN suspensionReason TEXT NOT NULL DEFAULT ''
            """.trimIndent()
        )

        db.execSQL(
            """
            ALTER TABLE medications
            ADD COLUMN suspendedAt INTEGER
            """.trimIndent()
        )
    }
}


val MIGRATION_2_3 = object : Migration(2, 3) {

    override fun migrate(
        db: SupportSQLiteDatabase
    ) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS suspension_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                medicationId INTEGER NOT NULL,
                reason TEXT NOT NULL,
                suspendedAt INTEGER NOT NULL,
                resumedAt INTEGER
            )
            """.trimIndent()
        )
    }
}


val MIGRATION_3_4 = object : Migration(3, 4) {

    override fun migrate(
        db: SupportSQLiteDatabase
    ) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS medication_schedules (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                medicationId INTEGER NOT NULL,
                dose TEXT NOT NULL,
                time TEXT NOT NULL
            )
            """.trimIndent()
        )
    }
}


val MIGRATION_4_5 = object : Migration(4, 5) {

    override fun migrate(
        db: SupportSQLiteDatabase
    ) {
        db.execSQL(
            """
            ALTER TABLE medication_schedules
            ADD COLUMN doseUnit TEXT NOT NULL DEFAULT 'tablet'
            """.trimIndent()
        )
    }
}


val MIGRATION_6_7 = object : Migration(6, 7) {

    override fun migrate(
        db: SupportSQLiteDatabase
    ) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS medication_doses (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                medicationId INTEGER NOT NULL,
                scheduleId INTEGER,
                dose TEXT NOT NULL,
                doseUnit TEXT NOT NULL,
                takenAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}


val MIGRATION_7_8 = object : Migration(7, 8) {

    override fun migrate(
        db: SupportSQLiteDatabase
    ) {

        // ---------------------------------------------------------
        // medications
        // quantity: TEXT -> REAL
        // ---------------------------------------------------------

        db.execSQL(
            """
            CREATE TABLE medications_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                strength TEXT NOT NULL,
                quantity REAL NOT NULL,
                quantityUnit TEXT NOT NULL,
                status TEXT NOT NULL,
                suspensionReason TEXT NOT NULL,
                suspendedAt INTEGER
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO medications_new (
                id,
                name,
                strength,
                quantity,
                quantityUnit,
                status,
                suspensionReason,
                suspendedAt
            )
            SELECT
                id,
                name,
                strength,
                CAST(quantity AS REAL),
                quantityUnit,
                status,
                suspensionReason,
                suspendedAt
            FROM medications
            """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE medications
            """.trimIndent()
        )

        db.execSQL(
            """
            ALTER TABLE medications_new
            RENAME TO medications
            """.trimIndent()
        )


        // ---------------------------------------------------------
        // medication_schedules
        // dose: TEXT -> REAL
        // ---------------------------------------------------------

        db.execSQL(
            """
            CREATE TABLE medication_schedules_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                medicationId INTEGER NOT NULL,
                dose REAL NOT NULL,
                doseUnit TEXT NOT NULL,
                time TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO medication_schedules_new (
                id,
                medicationId,
                dose,
                doseUnit,
                time
            )
            SELECT
                id,
                medicationId,
                CAST(dose AS REAL),
                doseUnit,
                time
            FROM medication_schedules
            """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE medication_schedules
            """.trimIndent()
        )

        db.execSQL(
            """
            ALTER TABLE medication_schedules_new
            RENAME TO medication_schedules
            """.trimIndent()
        )


        // ---------------------------------------------------------
        // medication_doses
        // dose: TEXT -> REAL
        // ---------------------------------------------------------

        db.execSQL(
            """
            CREATE TABLE medication_doses_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                medicationId INTEGER NOT NULL,
                scheduleId INTEGER,
                dose REAL NOT NULL,
                doseUnit TEXT NOT NULL,
                takenAt INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO medication_doses_new (
                id,
                medicationId,
                scheduleId,
                dose,
                doseUnit,
                takenAt
            )
            SELECT
                id,
                medicationId,
                scheduleId,
                CAST(dose AS REAL),
                doseUnit,
                takenAt
            FROM medication_doses
            """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE medication_doses
            """.trimIndent()
        )

        db.execSQL(
            """
            ALTER TABLE medication_doses_new
            RENAME TO medication_doses
            """.trimIndent()
        )
    }
}


class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        val database = Room.databaseBuilder(
            applicationContext,
            RefillIQDatabase::class.java,
            "refilliq_database"
        )
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
                MIGRATION_6_7,
                MIGRATION_7_8
            )
            .build()

        val repository = MedicationRepository(
            database.medicationDao(),
            database.suspensionHistoryDao(),
            database.medicationScheduleDao(),
            database.medicationDoseDao()
        )

        setContent {

            RefillIQTheme {

                var selectedMedication by remember {
                    mutableStateOf<Medication?>(null)
                }

                var historyMedication by remember {
                    mutableStateOf<Medication?>(null)
                }

                var doseHistoryMedication by remember {
                    mutableStateOf<Medication?>(null)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    when {

                        doseHistoryMedication != null -> {

                            MedicationDoseHistoryScreen(
                                medication =
                                    doseHistoryMedication!!,

                                repository =
                                    repository,

                                onBack = {
                                    doseHistoryMedication = null
                                }
                            )
                        }

                        historyMedication != null -> {

                            MedicationHistoryScreen(
                                medication =
                                    historyMedication!!,

                                repository =
                                    repository,

                                onBack = {
                                    historyMedication = null
                                }
                            )
                        }

                        selectedMedication != null -> {

                            SetMedicationScheduleScreen(
                                medicationId =
                                    selectedMedication!!.id,

                                medicationName =
                                    selectedMedication!!.name,

                                strength =
                                    selectedMedication!!.strength,

                                repository =
                                    repository,

                                onSave = {
                                    selectedMedication = null
                                },

                                onCancel = {
                                    selectedMedication = null
                                }
                            )
                        }

                        else -> {

                            AddMedicationScreen(
                                repository =
                                    repository,

                                onSetSchedule = { medication ->

                                    selectedMedication =
                                        medication
                                },

                                onMedicationHistory = { medication ->

                                    historyMedication =
                                        medication
                                },

                                onMedicationDoseHistory = { medication ->

                                    doseHistoryMedication =
                                        medication
                                },

                                modifier =
                                    Modifier.padding(
                                        innerPadding
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}