package com.refilliq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.refilliq.app.ui.theme.RefillIQTheme
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = Room.databaseBuilder(
            applicationContext,
            RefillIQDatabase::class.java,
            "refilliq_database"
        ).build()

        val repository = MedicationRepository(
            database.medicationDao()
        )

        setContent {
            RefillIQTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Greeting(
                        repository = repository,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    repository: MedicationRepository,
    modifier: Modifier = Modifier
) {
    var medicationName by remember { mutableStateOf("") }
    var strength by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var dailyUsage by remember { mutableStateOf("") }
    var savedMessage by remember { mutableStateOf("") }
    var medicationList by remember { mutableStateOf("") }
    val scope = remember { CoroutineScope(Dispatchers.IO) }
    val repositoryRef = repository
    var medications by remember {
        mutableStateOf(listOf<Medication>())
    }

    var isLoaded by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (!isLoaded) {
            LaunchedEffect(Unit) {
                medications = repositoryRef.getAllMedications()
                isLoaded = true
            }
        }
        Text(text = "Add Medication")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = medicationName,
            onValueChange = { medicationName = it },
            label = { Text("Medication Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = strength,
            onValueChange = { strength = it },
            label = { Text("Strength") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Quantity") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = dailyUsage,
            onValueChange = { dailyUsage = it },
            label = { Text("Daily Usage") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val medication = Medication(
                    name = medicationName,
                    strength = strength,
                    quantity = quantity,
                    dailyUsage = dailyUsage
                )

                scope.launch {
                    repositoryRef.insertMedication(medication)
                    medications = repositoryRef.getAllMedications()
                }
                savedMessage =
                    "Medication: $medicationName\n" +
                            "Strength: $strength\n" +
                            "Quantity: $quantity\n" +
                            "Daily Usage: $dailyUsage"

                medicationList +=
                    "\n\nMedication: $medicationName\n" +
                            "Strength: $strength"

                medicationName = ""
                strength = ""
                quantity = ""
                dailyUsage = ""
            }
        ) {
            Text("Save")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = savedMessage
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = medicationList
        )
    }
}