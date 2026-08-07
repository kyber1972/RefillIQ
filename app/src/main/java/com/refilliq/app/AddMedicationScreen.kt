package com.refilliq.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddMedicationScreen(
    repository: MedicationRepository,
    modifier: Modifier = Modifier
) {

    var medicationName by remember { mutableStateOf("") }
    var strength by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var dailyUsage by remember { mutableStateOf("") }

    var savedMessage by remember { mutableStateOf("") }
    var medications by remember {
        mutableStateOf<List<Medication>>(emptyList())
    }

    val scope = rememberCoroutineScope()
   LaunchedEffect(Unit) {
       repository
            .getAllMedications()
           .collectLatest {
                medications = it
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

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
                    repository.insertMedication(medication)
                }

                savedMessage =
                    "Medication: $medicationName\n" +
                            "Strength: $strength\n" +
                            "Quantity: $quantity\n" +
                            "Daily Usage: $dailyUsage"


                medicationName = ""
                strength = ""
                quantity = ""
                dailyUsage = ""

            }
        ) {
            Text("Save")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = savedMessage)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Total medications: ${medications.size}")

    }
}