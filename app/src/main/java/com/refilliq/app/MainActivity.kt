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
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
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
                MIGRATION_4_5
            )
            .build()

        val repository = MedicationRepository(
            database.medicationDao(),
            database.suspensionHistoryDao(),
            database.medicationScheduleDao()
        )

        setContent {

            RefillIQTheme {

                var selectedMedication by remember {
                    mutableStateOf<Medication?>(null)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    val medication = selectedMedication

                    if (medication == null) {

                        AddMedicationScreen(
                            repository = repository,
                            onSetSchedule = { selected ->
                                selectedMedication = selected
                            },
                            modifier = Modifier.padding(innerPadding)
                        )

                    } else {

                        SetMedicationScheduleScreen(
                            medicationId = medication.id,
                            medicationName = medication.name,
                            strength = medication.strength,
                            repository = repository,
                            onSave = {
                                selectedMedication = null
                            },
                            onCancel = {
                                selectedMedication = null
                            }
                        )
                    }
                }
            }
        }
    }
}