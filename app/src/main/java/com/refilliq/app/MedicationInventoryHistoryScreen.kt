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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MedicationInventoryHistoryScreen(
    medication: Medication,
    repository: MedicationRepository,
    onBack: () -> Unit
) {

    val movements by repository
        .getInventoryMovementsForMedication(
            medication.id
        )
        .collectAsState(
            initial = emptyList()
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "Inventory History",
                style = MaterialTheme.typography.headlineSmall
            )

            TextButton(
                onClick = onBack
            ) {
                Text("Back")
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = medication.name,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text =
                "${medication.strength} • ${medication.quantityUnit}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        if (movements.isEmpty()) {

            Text(
                text = "No inventory movements recorded.",
                style = MaterialTheme.typography.bodyMedium
            )

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {

                items(
                    items = movements,
                    key = { movement ->
                        movement.id
                    }
                ) { movement ->

                    val dateText =
                        SimpleDateFormat(
                            "MMM dd, yyyy • HH:mm",
                            Locale.getDefault()
                        ).format(
                            Date(movement.createdAt)
                        )

                    val quantityText =
                        if (movement.type == "IN") {
                            "+${movement.quantity}"
                        } else {
                            "-${movement.quantity}"
                        }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 6.dp
                            )
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Row(
                                modifier =
                                    Modifier.fillMaxWidth(),
                                horizontalArrangement =
                                    Arrangement.SpaceBetween
                            ) {

                                Text(
                                    text =
                                        if (
                                            movement.type == "IN"
                                        ) {
                                            "IN"
                                        } else {
                                            "OUT"
                                        },

                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium
                                )

                                Text(
                                    text =
                                        "$quantityText ${medication.quantityUnit}",

                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Text(
                                text = "Reason",
                                style =
                                    MaterialTheme
                                        .typography
                                        .labelMedium
                            )

                            Text(
                                text = movement.reason,
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(6.dp)
                            )

                            Text(
                                text = dateText,
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}