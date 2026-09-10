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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

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

    val scope = rememberCoroutineScope()

    var calculatedInventory by remember {
        mutableStateOf<Double?>(null)
    }

    var showCorrectionDialog by remember {
        mutableStateOf(false)
    }

    var correctionQuantity by remember {
        mutableStateOf("")
    }

    var correctionIncrease by remember {
        mutableStateOf(true)
    }

    var correctionMessage by remember {
        mutableStateOf<String?>(null)
    }

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

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Inventory Integrity",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Current inventory: " +
                                "${medication.quantity} ${medication.quantityUnit}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Button(
                    onClick = {

                        scope.launch {

                            calculatedInventory =
                                repository.calculateInventoryFromHistory(
                                    medicationId = medication.id
                                )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Verify Inventory")
                }

                calculatedInventory?.let { calculated ->

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Calculated from history: " +
                                    "$calculated ${medication.quantityUnit}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    val matches =
                        abs(
                            medication.quantity - calculated
                        ) < 0.000001

                    Text(
                        text =
                            if (matches) {
                                "Status: MATCH"
                            } else {
                                "Status: MISMATCH"
                            },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = {
                correctionQuantity = ""
                correctionIncrease = true
                correctionMessage = null
                showCorrectionDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Inventory Correction")
        }

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
                        when (movement.type) {
                            "OPENING_BALANCE" ->
                                "+${movement.quantity}"

                            "IN" ->
                                "+${movement.quantity}"

                            "ADJUSTMENT" ->
                                if (movement.quantity >= 0.0) {
                                    "+${movement.quantity}"
                                } else {
                                    "${movement.quantity}"
                                }

                            else ->
                                "-${movement.quantity}"
                        }

                    val movementType =
                        when (movement.type) {
                            "OPENING_BALANCE" ->
                                "OPENING BALANCE"

                            "IN" ->
                                "IN"

                            "ADJUSTMENT" ->
                                "ADJUSTMENT"

                            else ->
                                "OUT"
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
                                    text = movementType,
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

    if (showCorrectionDialog) {

        AlertDialog(
            onDismissRequest = {
                showCorrectionDialog = false
                correctionMessage = null
            },

            title = {
                Text("Inventory Correction")
            },

            text = {

                Column {

                    Text(
                        text =
                            "Current inventory: ${medication.quantity} ${medication.quantityUnit}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        TextButton(
                            onClick = {
                                correctionIncrease = true
                            }
                        ) {
                            Text(
                                if (correctionIncrease) {
                                    "Increase ✓"
                                } else {
                                    "Increase"
                                }
                            )
                        }

                        TextButton(
                            onClick = {
                                correctionIncrease = false
                            }
                        ) {
                            Text(
                                if (!correctionIncrease) {
                                    "Decrease ✓"
                                } else {
                                    "Decrease"
                                }
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    OutlinedTextField(
                        value = correctionQuantity,
                        onValueChange = {
                            correctionQuantity = it
                            correctionMessage = null
                        },
                        label = {
                            Text("Quantity")
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Reason: Inventory correction",
                        style = MaterialTheme.typography.bodySmall
                    )

                    correctionMessage?.let { message ->

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        val quantity =
                            correctionQuantity
                                .replace(',', '.')
                                .toDoubleOrNull()

                        if (
                            quantity == null ||
                            quantity <= 0.0
                        ) {

                            correctionMessage =
                                "Enter a quantity greater than zero."

                            return@TextButton
                        }

                        val adjustment =
                            if (correctionIncrease) {
                                quantity
                            } else {
                                -quantity
                            }

                        scope.launch {

                            val success =
                                repository.recordInventoryAdjustment(
                                    medicationId = medication.id,
                                    adjustment = adjustment,
                                    reason = "Inventory correction",
                                    createdAt =
                                        System.currentTimeMillis()
                                )

                            if (success) {

                                calculatedInventory = null
                                showCorrectionDialog = false
                                correctionQuantity = ""
                                correctionMessage = null

                            } else {

                                correctionMessage =
                                    if (correctionIncrease) {
                                        "Unable to apply the inventory correction."
                                    } else {
                                        "Insufficient inventory for this correction."
                                    }
                            }
                        }
                    }
                ) {
                    Text("Apply")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showCorrectionDialog = false
                        correctionMessage = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
