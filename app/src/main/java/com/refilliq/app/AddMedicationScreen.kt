package com.refilliq.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AddMedicationScreen(
    repository: MedicationRepository,
    onSetSchedule: (Medication) -> Unit,
    onMedicationHistory: (Medication) -> Unit,
    modifier: Modifier = Modifier
) {

    var medicationName by remember {
        mutableStateOf("")
    }

    var strength by remember {
        mutableStateOf("")
    }

    var quantity by remember {
        mutableStateOf("")
    }

    var quantityUnit by remember {
        mutableStateOf("tablet")
    }

    var quantityUnitMenuExpanded by remember {
        mutableStateOf(false)
    }

    var savedMessage by remember {
        mutableStateOf("")
    }

    var showSuspendDialog by remember {
        mutableStateOf(false)
    }

    var showResumeDialog by remember {
        mutableStateOf(false)
    }

    var suspensionReason by remember {
        mutableStateOf("")
    }

    var suspensionError by remember {
        mutableStateOf("")
    }

    var selectedMedication by remember {
        mutableStateOf<Medication?>(null)
    }

    var selectedSuspensionId by remember {
        mutableStateOf<Int?>(null)
    }

    var medications by remember {
        mutableStateOf<List<Medication>>(emptyList())
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {

        repository
            .getAllMedications()
            .collect { medicationList ->

                medications = medicationList
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

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = medicationName,
            onValueChange = {
                medicationName = it
            },
            label = {
                Text("Medication Name")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = strength,
            onValueChange = {
                strength = it
            },
            label = {
                Text("Strength")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = quantity,
            onValueChange = {

                if (
                    it.isEmpty() ||
                    it.matches(
                        Regex("^\\d*\\.?\\d*$")
                    )
                ) {
                    quantity = it
                }
            },
            label = {
                Text("Quantity")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Quantity Unit",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                onClick = {
                    quantityUnitMenuExpanded = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = quantityUnit
                )
            }

            DropdownMenu(
                expanded = quantityUnitMenuExpanded,
                onDismissRequest = {
                    quantityUnitMenuExpanded = false
                }
            ) {

                DropdownMenuItem(
                    text = {
                        Text("Tablet")
                    },
                    onClick = {
                        quantityUnit = "tablet"
                        quantityUnitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Pill")
                    },
                    onClick = {
                        quantityUnit = "pill"
                        quantityUnitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Capsule")
                    },
                    onClick = {
                        quantityUnit = "capsule"
                        quantityUnitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Softgel")
                    },
                    onClick = {
                        quantityUnit = "softgel"
                        quantityUnitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("mL")
                    },
                    onClick = {
                        quantityUnit = "mL"
                        quantityUnitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Drop")
                    },
                    onClick = {
                        quantityUnit = "drop"
                        quantityUnitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Puff")
                    },
                    onClick = {
                        quantityUnit = "puff"
                        quantityUnitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Injection")
                    },
                    onClick = {
                        quantityUnit = "injection"
                        quantityUnitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Packet")
                    },
                    onClick = {
                        quantityUnit = "packet"
                        quantityUnitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Suppository")
                    },
                    onClick = {
                        quantityUnit = "suppository"
                        quantityUnitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("tsp")
                    },
                    onClick = {
                        quantityUnit = "tsp"
                        quantityUnitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("tbsp")
                    },
                    onClick = {
                        quantityUnit = "tbsp"
                        quantityUnitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Other")
                    },
                    onClick = {
                        quantityUnit = "other"
                        quantityUnitMenuExpanded = false
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = {

                val medication = Medication(
                    name = medicationName.trim(),
                    strength = strength.trim(),
                    quantity = quantity.trim(),
                    quantityUnit = quantityUnit
                )

                scope.launch {

                    repository.insertMedication(
                        medication
                    )
                }

                savedMessage =
                    "Medication: $medicationName\n" +
                            "Strength: $strength\n" +
                            "Quantity: $quantity $quantityUnit"

                medicationName = ""
                strength = ""
                quantity = ""
                quantityUnit = "tablet"
            }
        ) {
            Text("Save")
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = savedMessage
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Medications",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            items(medications) { medication ->

                var menuExpanded by remember {
                    mutableStateOf(false)
                }

                val schedules by repository
                    .getSchedulesForMedication(
                        medication.id
                    )
                    .collectAsState(
                        initial = emptyList()
                    )

                val isSuspended =
                    medication.status == "SUSPENDED"

                val medicationQuantity =
                    medication.quantity
                        .toDoubleOrNull()
                        ?: 0.0

                val normalizedQuantityUnit =
                    medication.quantityUnit
                        .trim()
                        .lowercase()

                val dailyUsage =
                    schedules
                        .filter {

                            it.doseUnit
                                .trim()
                                .lowercase() ==
                                    normalizedQuantityUnit
                        }
                        .sumOf {

                            it.dose
                                .toDoubleOrNull()
                                ?: 0.0
                        }

                val daysRemaining =
                    if (
                        medicationQuantity > 0.0 &&
                        dailyUsage > 0.0
                    ) {

                        (
                                medicationQuantity /
                                        dailyUsage
                                ).toInt()

                    } else {

                        0
                    }

                val inventoryStatus =
                    when {

                        isSuspended ->
                            "Suspended"

                        daysRemaining <= 7 ->
                            "Almost empty"

                        daysRemaining <= 14 ->
                            "Running low"

                        else ->
                            "In stock"
                    }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = medication.name,
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium
                            )

                            Box {

                                TextButton(
                                    onClick = {
                                        menuExpanded = true
                                    }
                                ) {
                                    Text("⋮")
                                }

                                DropdownMenu(
                                    expanded =
                                        menuExpanded,
                                    onDismissRequest = {
                                        menuExpanded =
                                            false
                                    }
                                ) {

                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Edit medication"
                                            )
                                        },
                                        onClick = {
                                            menuExpanded =
                                                false
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Medication History"
                                            )
                                        },
                                        onClick = {

                                            menuExpanded =
                                                false

                                            onMedicationHistory(
                                                medication
                                            )
                                        }
                                    )

                                    if (isSuspended) {

                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    "Resume medication"
                                                )
                                            },
                                            onClick = {

                                                menuExpanded =
                                                    false

                                                selectedMedication =
                                                    medication

                                                selectedSuspensionId =
                                                    null

                                                scope.launch {

                                                    val history =
                                                        repository
                                                            .getSuspensionHistory(
                                                                medication.id
                                                            )

                                                    val activeSuspension =
                                                        history.firstOrNull {
                                                            it.resumedAt ==
                                                                    null
                                                        }

                                                    if (
                                                        activeSuspension !=
                                                        null
                                                    ) {

                                                        selectedSuspensionId =
                                                            activeSuspension
                                                                .id

                                                        showResumeDialog =
                                                            true
                                                    }
                                                }
                                            }
                                        )

                                    } else {

                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    "Suspend medication"
                                                )
                                            },
                                            onClick = {

                                                menuExpanded =
                                                    false

                                                selectedMedication =
                                                    medication

                                                suspensionReason =
                                                    ""

                                                suspensionError =
                                                    ""

                                                showSuspendDialog =
                                                    true
                                            }
                                        )
                                    }

                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Delete medication"
                                            )
                                        },
                                        onClick = {
                                            menuExpanded =
                                                false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = medication.strength,
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            Column {

                                Text(
                                    text = "Quantity",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .labelMedium
                                )

                                Text(
                                    text =
                                        "${medication.quantity} " +
                                                medication.quantityUnit,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyLarge
                                )
                            }

                            Column {

                                Text(
                                    text = "Daily usage",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .labelMedium
                                )

                                Text(
                                    text =
                                        if (
                                            dailyUsage > 0.0
                                        ) {

                                            "$dailyUsage " +
                                                    "${medication.quantityUnit} / day"

                                        } else {

                                            "Not configured"
                                        },
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        if (isSuspended) {

                            InventoryStatusBadge(
                                status = "Suspended"
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = "Reason:",
                                style =
                                    MaterialTheme
                                        .typography
                                        .labelMedium
                            )

                            Text(
                                text =
                                    medication.suspensionReason,
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )

                        } else {

                            Text(
                                text =
                                    "$daysRemaining days remaining",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            InventoryStatusBadge(
                                status = inventoryStatus
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        Text(
                            text = "Schedule",
                            style =
                                MaterialTheme
                                    .typography
                                    .labelMedium
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        if (schedules.isEmpty()) {

                            Text(
                                text = "Not configured",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )

                        } else {

                            schedules.forEach { schedule ->

                                Row(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                vertical = 4.dp
                                            ),
                                    horizontalArrangement =
                                        Arrangement
                                            .SpaceBetween
                                ) {

                                    Text(
                                        text =
                                            schedule.time,
                                        style =
                                            MaterialTheme
                                                .typography
                                                .bodyMedium
                                    )

                                    Text(
                                        text =
                                            "${schedule.dose} " +
                                                    schedule.doseUnit,
                                        style =
                                            MaterialTheme
                                                .typography
                                                .bodyMedium
                                    )
                                }
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        TextButton(
                            onClick = {
                                onSetSchedule(medication)
                            }
                        ) {
                            Text("Set schedule")
                        }
                    }
                }
            }
        }

        if (showSuspendDialog) {

            AlertDialog(
                onDismissRequest = {
                    showSuspendDialog = false
                },
                title = {
                    Text("Suspend medication")
                },
                text = {

                    Column {

                        Text(
                            text =
                                "Why is this medication being suspended?"
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        OutlinedTextField(
                            value =
                                suspensionReason,
                            onValueChange = {

                                suspensionReason = it

                                if (
                                    suspensionError.isNotEmpty()
                                ) {
                                    suspensionError = ""
                                }
                            },
                            label = {
                                Text("Reason")
                            },
                            isError =
                                suspensionError.isNotEmpty(),
                            supportingText = {

                                if (
                                    suspensionError.isNotEmpty()
                                ) {
                                    Text(
                                        suspensionError
                                    )
                                }
                            },
                            modifier =
                                Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {

                    TextButton(
                        onClick = {

                            val reason =
                                suspensionReason.trim()

                            if (reason.isBlank()) {

                                suspensionError =
                                    "Please enter a reason."

                            } else {

                                selectedMedication?.let {
                                        medication ->

                                    scope.launch {

                                        repository
                                            .suspendMedication(
                                                medicationId =
                                                    medication.id,
                                                reason =
                                                    reason,
                                                suspendedAt =
                                                    System
                                                        .currentTimeMillis()
                                            )

                                        showSuspendDialog =
                                            false

                                        suspensionReason =
                                            ""

                                        suspensionError =
                                            ""

                                        selectedMedication =
                                            null
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Suspend")
                    }
                },
                dismissButton = {

                    TextButton(
                        onClick = {
                            showSuspendDialog =
                                false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showResumeDialog) {

            AlertDialog(
                onDismissRequest = {

                    showResumeDialog = false
                    selectedSuspensionId = null
                    selectedMedication = null
                },
                title = {
                    Text("Resume medication")
                },
                text = {

                    Text(
                        text =
                            "Resume ${
                                selectedMedication?.name
                                    ?: "this medication"
                            }?"
                    )
                },
                confirmButton = {

                    TextButton(
                        onClick = {

                            val medication =
                                selectedMedication

                            val suspensionId =
                                selectedSuspensionId

                            if (
                                medication != null &&
                                suspensionId != null
                            ) {

                                scope.launch {

                                    repository
                                        .resumeMedication(
                                            medicationId =
                                                medication.id,
                                            suspensionId =
                                                suspensionId,
                                            resumedAt =
                                                System
                                                    .currentTimeMillis()
                                        )

                                    showResumeDialog =
                                        false

                                    selectedSuspensionId =
                                        null

                                    selectedMedication =
                                        null
                                }
                            }
                        }
                    ) {
                        Text("Resume")
                    }
                },
                dismissButton = {

                    TextButton(
                        onClick = {

                            showResumeDialog = false
                            selectedSuspensionId = null
                            selectedMedication = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun InventoryStatusBadge(
    status: String
) {

    val statusColor =
        when (status) {

            "Almost empty" ->
                Color.Red

            "Suspended" ->
                Color.Red

            "Running low" ->
                Color(0xFFFFA000)

            else ->
                Color(0xFF2E7D32)
        }

    Surface(
        color =
            statusColor.copy(alpha = 0.12f),
        shape =
            RoundedCornerShape(50)
    ) {

        Text(
            text = status,
            color = statusColor,
            style =
                MaterialTheme.typography.labelMedium,
            modifier =
                Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 6.dp
                )
        )
    }
}