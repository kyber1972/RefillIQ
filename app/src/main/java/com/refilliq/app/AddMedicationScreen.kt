package com.refilliq.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color

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

        Text(
            text = "Add Medication",
            style = MaterialTheme.typography.headlineSmall
        )

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

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Medications",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(medications) { medication ->

                val quantity = medication.quantity.toIntOrNull() ?: 0
                val dailyUsage = medication.dailyUsage.toIntOrNull() ?: 0

                val daysRemaining =
                    if (dailyUsage > 0) {
                        quantity / dailyUsage
                    } else {
                        0
                    }
                val inventoryStatus =
                    when {
                        daysRemaining <= 7 -> "Almost empty"
                        daysRemaining <= 14 -> "Running low"
                        else -> "In stock"
                    }
                val inventoryColor =
                    when (inventoryStatus) {
                        "Almost empty" -> Color.Red
                        "Running low" -> Color(0xFFFFA000)
                        else -> Color(0xFF2E7D32)
                    }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = medication.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = medication.strength,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Column {
                                Text(
                                    text = "Quantity",
                                    style = MaterialTheme.typography.labelMedium
                                )

                                Text(
                                    text = medication.quantity,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            Column {
                                Text(
                                    text = "Daily usage",
                                    style = MaterialTheme.typography.labelMedium
                                )

                                Text(
                                    text = "${medication.dailyUsage} / day",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "$daysRemaining days remaining",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = inventoryStatus,
                            style = MaterialTheme.typography.bodyMedium,
                            color = inventoryColor
                        )
                    }
                }
            }
        }
    }
}