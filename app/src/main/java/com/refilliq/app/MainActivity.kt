package com.refilliq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.refilliq.app.ui.theme.RefillIQTheme
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(
        database: SupportSQLiteDatabase
    ) {
        database.execSQL(
            """
            ALTER TABLE medications
            ADD COLUMN status TEXT NOT NULL DEFAULT 'ACTIVE'
            """.trimIndent()
        )

        database.execSQL(
            """
            ALTER TABLE medications
            ADD COLUMN suspensionReason TEXT NOT NULL DEFAULT ''
            """.trimIndent()
        )

        database.execSQL(
            """
            ALTER TABLE medications
            ADD COLUMN suspendedAt INTEGER
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
            .addMigrations(MIGRATION_1_2)
            .build()

        val repository = MedicationRepository(
            database.medicationDao()
        )

        setContent {

            RefillIQTheme {

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    AddMedicationScreen(
                        repository = repository,
                        modifier = Modifier.padding(innerPadding)
                    )

                }

            }

        }

    }

}