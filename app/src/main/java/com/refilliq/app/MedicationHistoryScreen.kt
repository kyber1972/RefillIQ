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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MedicationHistoryScreen(
    medication: Medication,
    repository: MedicationRepository,
    onBack: () -> Unit
) {

    var history by remember {
        mutableStateOf<List<SuspensionHistory>>(emptyList())
    }

    LaunchedEffect(medication.id) {

        history =
            repository.getSuspensionHistory(
                medication.id
            )
    }

    val dateFormat =
        remember {
            SimpleDateFormat(
                "MMM dd, yyyy - HH:mm",
                Locale.getDefault()
            )
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Medication History",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = medication.name,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = medication.strength,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (history.isEmpty()) {

            Text(
                text = "No history available.",
                style = MaterialTheme.typography.bodyMedium
            )

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                items(history) { item ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = "Suspended",
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Text(
                                text =
                                    "Date: ${
                                        dateFormat.format(
                                            Date(item.suspendedAt)
                                        )
                                    }",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(
                                text =
                                    "Reason: ${item.reason}",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(12.dp)
                            )

                            if (item.resumedAt != null) {

                                Text(
                                    text =
                                        "Resumed: ${
                                            dateFormat.format(
                                                Date(
                                                    item.resumedAt
                                                )
                                            )
                                        }",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium
                                )

                            } else {

                                Text(
                                    text =
                                        "Status: Still suspended",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {

            TextButton(
                onClick = onBack
            ) {
                Text("Back")
            }
        }
    }
}