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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun AddMedicationScreen(
    repository: MedicationRepository,
    onSetSchedule: (Medication) -> Unit,
    onMedicationHistory: (Medication) -> Unit,
    onMedicationDoseHistory: (Medication) -> Unit,
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

    var showEditDialog by remember {
        mutableStateOf(false)
    }

    var suspensionReason by remember {
        mutableStateOf("")
    }

    var suspensionError by remember {
        mutableStateOf("")
    }

    var editMedicationName by remember {
        mutableStateOf("")
    }

    var editStrength by remember {
        mutableStateOf("")
    }

    var editQuantity by remember {
        mutableStateOf("")
    }

    var editQuantityUnit by remember {
        mutableStateOf("tablet")
    }

    var editQuantityUnitMenuExpanded by remember {
        mutableStateOf(false)
    }

    var editError by remember {
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
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
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

                val units = listOf(
                    "tablet",
                    "pill",
                    "capsule",
                    "softgel",
                    "mL",
                    "drop",
                    "puff",
                    "injection",
                    "packet",
                    "suppository",
                    "tsp",
                    "tbsp",
                    "other"
                )

                units.forEach { unit ->

                    DropdownMenuItem(
                        text = {
                            Text(unit)
                        },
                        onClick = {

                            quantityUnit = unit

                            quantityUnitMenuExpanded =
                                false
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = {

                val quantityValue =
                    quantity.trim().toDoubleOrNull()

                if (
                    medicationName.isBlank() ||
                    strength.isBlank() ||
                    quantityValue == null ||
                    quantityValue < 0.0
                ) {

                    savedMessage =
                        "Please enter valid medication information."

                    return@Button
                }

                val medication =
                    Medication(
                        name =
                            medicationName.trim(),

                        strength =
                            strength.trim(),

                        quantity =
                            quantityValue,

                        quantityUnit =
                            quantityUnit
                    )

                scope.launch {

                    repository.insertMedication(
                        medication
                    )
                }

                savedMessage =
                    "Medication: ${medicationName.trim()}\n" +
                            "Strength: ${strength.trim()}\n" +
                            "Quantity: $quantityValue $quantityUnit"

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

                /*
                 * All dose events for this medication.
                 *
                 * Because this is a Flow, the UI updates
                 * automatically whenever a new Taken is recorded.
                 */
                val doses by repository
                    .getDosesForMedication(
                        medication.id
                    )
                    .collectAsState(
                        initial = emptyList()
                    )

                val isSuspended =
                    medication.status == "SUSPENDED"

                val medicationQuantity =
                    medication.quantity

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

                        /*
                         * MEDICATION HEADER
                         */
                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            Text(
                                text =
                                    medication.name,

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
                                        menuExpanded = false
                                    }
                                ) {

                                    /*
                                     * EDIT MEDICATION
                                     *
                                     * Existing functionality preserved.
                                     */
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Edit medication"
                                            )
                                        },
                                        onClick = {

                                            menuExpanded =
                                                false

                                            editMedicationName =
                                                medication.name

                                            editStrength =
                                                medication.strength

                                            editQuantity =
                                                medication.quantity
                                                    .toString()

                                            editQuantityUnit =
                                                medication.quantityUnit

                                            editError = ""

                                            selectedMedication =
                                                medication

                                            showEditDialog =
                                                true
                                        }
                                    )

                                    /*
                                     * MEDICATION HISTORY
                                     */
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

                                    /*
                                     * DOSE HISTORY
                                     */
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Dose History"
                                            )
                                        },
                                        onClick = {

                                            menuExpanded =
                                                false

                                            onMedicationDoseHistory(
                                                medication
                                            )
                                        }
                                    )

                                    /*
                                     * RESUME / SUSPEND
                                     */
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
                                                        history
                                                            .firstOrNull {
                                                                it.resumedAt ==
                                                                        null
                                                            }

                                                    if (
                                                        activeSuspension !=
                                                        null
                                                    ) {

                                                        selectedSuspensionId =
                                                            activeSuspension.id

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

                                    /*
                                     * DELETE
                                     *
                                     * Existing behavior preserved.
                                     */
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
                            text =
                                medication.strength,

                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        /*
                         * QUANTITY / DAILY USAGE
                         */
                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),

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

                        /*
                         * INVENTORY STATUS
                         *
                         * Inventory is NOT changed yet.
                         */
                        if (isSuspended) {

                            InventoryStatusBadge(
                                status = "Suspended"
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
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
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            InventoryStatusBadge(
                                status =
                                    inventoryStatus
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        /*
                         * SCHEDULE
                         */
                        Text(
                            text = "Schedule",
                            style =
                                MaterialTheme
                                    .typography
                                    .labelMedium
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
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

                            /*
                             * TODAY RANGE
                             */
                            val startOfDay =
                                remember {
                                    Calendar.getInstance()
                                        .apply {

                                            set(
                                                Calendar.HOUR_OF_DAY,
                                                0
                                            )

                                            set(
                                                Calendar.MINUTE,
                                                0
                                            )

                                            set(
                                                Calendar.SECOND,
                                                0
                                            )

                                            set(
                                                Calendar.MILLISECOND,
                                                0
                                            )
                                        }
                                        .timeInMillis
                                }

                            val endOfDay =
                                remember(startOfDay) {

                                    val calendar =
                                        Calendar.getInstance()

                                    calendar.timeInMillis =
                                        startOfDay

                                    calendar.add(
                                        Calendar.DAY_OF_YEAR,
                                        1
                                    )

                                    calendar.timeInMillis
                                }

                            schedules.forEach { schedule ->

                                /*
                                 * Count every Taken event for
                                 * this schedule today.
                                 *
                                 * There is intentionally NO
                                 * "already taken" restriction.
                                 */
                                val takenTodayCount =
                                    doses.count { dose ->

                                        dose.scheduleId ==
                                                schedule.id &&

                                                dose.takenAt >=
                                                startOfDay &&

                                                dose.takenAt <
                                                endOfDay
                                    }

                                Column(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                vertical = 6.dp
                                            )
                                ) {

                                    /*
                                     * TIME + DOSE
                                     */
                                    Row(
                                        modifier =
                                            Modifier.fillMaxWidth(),

                                        horizontalArrangement =
                                            Arrangement.SpaceBetween
                                    ) {

                                        Column {

                                            Text(
                                                text =
                                                    schedule.time,

                                                style =
                                                    MaterialTheme
                                                        .typography
                                                        .bodyLarge
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

                                        TextButton(
                                            onClick = {

                                                /*
                                                 * EDIT SCHEDULE
                                                 */
                                                onSetSchedule(
                                                    medication
                                                )
                                            }
                                        ) {
                                            Text("Edit")
                                        }
                                    }

                                    Spacer(
                                        modifier =
                                            Modifier.height(4.dp)
                                    )

                                    /*
                                     * TAKEN COUNT
                                     */
                                    if (
                                        takenTodayCount > 0
                                    ) {

                                        Text(
                                            text =
                                                "Taken today: " +
                                                        takenTodayCount,

                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .bodySmall
                                        )

                                        Spacer(
                                            modifier =
                                                Modifier.height(4.dp)
                                        )
                                    }

                                    /*
                                     * MARK AS TAKEN
                                     *
                                     * A schedule can be marked as taken
                                     * only once per day.
                                     *
                                     * The dose history still keeps every
                                     * previously recorded event.
                                     */
                                    val isTakenToday =
                                        takenTodayCount > 0

                                    Button(
                                        onClick = {

                                            scope.launch {

                                                val calendar = Calendar.getInstance().apply {
                                                    set(Calendar.HOUR_OF_DAY, 0)
                                                    set(Calendar.MINUTE, 0)
                                                    set(Calendar.SECOND, 0)
                                                    set(Calendar.MILLISECOND, 0)
                                                }

                                                val todayStart =
                                                    calendar.timeInMillis

                                                calendar.add(
                                                    Calendar.DAY_OF_YEAR,
                                                    1
                                                )

                                                val todayEnd =
                                                    calendar.timeInMillis

                                                val alreadyTakenToday =
                                                    doses.any { dose ->
                                                        dose.scheduleId ==
                                                                schedule.id &&
                                                                dose.takenAt >=
                                                                todayStart &&
                                                                dose.takenAt <
                                                                todayEnd
                                                    }

                                                if (!alreadyTakenToday) {

                                                    repository
                                                        .insertDose(
                                                            MedicationDose(
                                                                medicationId =
                                                                    medication.id,

                                                                scheduleId =
                                                                    schedule.id,

                                                                dose =
                                                                    schedule.dose,

                                                                doseUnit =
                                                                    schedule.doseUnit,

                                                                takenAt =
                                                                    System
                                                                        .currentTimeMillis()
                                                            )
                                                        )
                                                }
                                            }
                                        },

                                        enabled = !isTakenToday,

                                        modifier =
                                            Modifier.fillMaxWidth()
                                    ) {

                                        Text(
                                            if (isTakenToday) {
                                                "✓ Taken today"
                                            } else {
                                                "Mark as taken"
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        /*
                         * SET SCHEDULE
                         */
                        TextButton(
                            onClick = {
                                onSetSchedule(
                                    medication
                                )
                            }
                        ) {
                            Text("Set schedule")
                        }
                    }
                }
            }
        }

        /*
         * EDIT MEDICATION DIALOG
         */
        if (showEditDialog) {

            AlertDialog(
                onDismissRequest = {

                    showEditDialog = false
                    selectedMedication = null
                    editError = ""
                },

                title = {
                    Text("Edit Medication")
                },

                text = {

                    Column {

                        OutlinedTextField(
                            value =
                                editMedicationName,

                            onValueChange = {

                                editMedicationName =
                                    it

                                editError = ""
                            },

                            label = {
                                Text("Medication Name")
                            },

                            modifier =
                                Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        OutlinedTextField(
                            value =
                                editStrength,

                            onValueChange = {

                                editStrength =
                                    it

                                editError = ""
                            },

                            label = {
                                Text("Strength")
                            },

                            modifier =
                                Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        OutlinedTextField(
                            value =
                                editQuantity,

                            onValueChange = {

                                if (
                                    it.isEmpty() ||
                                    it.matches(
                                        Regex(
                                            "^\\d*\\.?\\d*$"
                                        )
                                    )
                                ) {

                                    editQuantity =
                                        it

                                    editError = ""
                                }
                            },

                            label = {
                                Text("Quantity")
                            },

                            keyboardOptions =
                                KeyboardOptions(
                                    keyboardType =
                                        KeyboardType.Decimal
                                ),

                            modifier =
                                Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text = "Quantity Unit",

                            style =
                                MaterialTheme
                                    .typography
                                    .labelLarge
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Box(
                            modifier =
                                Modifier.fillMaxWidth()
                        ) {

                            Button(
                                onClick = {

                                    editQuantityUnitMenuExpanded =
                                        true
                                },

                                modifier =
                                    Modifier.fillMaxWidth()
                            ) {

                                Text(
                                    editQuantityUnit
                                )
                            }

                            DropdownMenu(
                                expanded =
                                    editQuantityUnitMenuExpanded,

                                onDismissRequest = {

                                    editQuantityUnitMenuExpanded =
                                        false
                                }
                            ) {

                                val units =
                                    listOf(
                                        "tablet",
                                        "pill",
                                        "capsule",
                                        "softgel",
                                        "mL",
                                        "drop",
                                        "puff",
                                        "injection",
                                        "packet",
                                        "suppository",
                                        "tsp",
                                        "tbsp",
                                        "other"
                                    )

                                units.forEach { unit ->

                                    DropdownMenuItem(
                                        text = {
                                            Text(unit)
                                        },

                                        onClick = {

                                            editQuantityUnit =
                                                unit

                                            editQuantityUnitMenuExpanded =
                                                false
                                        }
                                    )
                                }
                            }
                        }

                        if (
                            editError.isNotEmpty()
                        ) {

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Text(
                                text =
                                    editError,

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .error,

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall
                            )
                        }
                    }
                },

                confirmButton = {

                    TextButton(
                        onClick = {

                            val medication =
                                selectedMedication

                            val quantityValue =
                                editQuantity
                                    .trim()
                                    .toDoubleOrNull()

                            if (
                                medication == null
                            ) {

                                editError =
                                    "Medication not found."

                                return@TextButton
                            }

                            if (
                                editMedicationName
                                    .isBlank()
                            ) {

                                editError =
                                    "Please enter a medication name."

                                return@TextButton
                            }

                            if (
                                editStrength
                                    .isBlank()
                            ) {

                                editError =
                                    "Please enter the strength."

                                return@TextButton
                            }

                            if (
                                quantityValue == null ||
                                quantityValue < 0.0
                            ) {

                                editError =
                                    "Please enter a valid quantity."

                                return@TextButton
                            }

                            scope.launch {

                                repository
                                    .updateMedication(
                                        medicationId =
                                            medication.id,

                                        name =
                                            editMedicationName
                                                .trim(),

                                        strength =
                                            editStrength
                                                .trim(),

                                        quantity =
                                            quantityValue,

                                        quantityUnit =
                                            editQuantityUnit
                                    )

                                showEditDialog =
                                    false

                                selectedMedication =
                                    null

                                editError = ""

                                savedMessage =
                                    "Medication updated successfully."
                            }
                        }
                    ) {
                        Text("Save")
                    }
                },

                dismissButton = {

                    TextButton(
                        onClick = {

                            showEditDialog =
                                false

                            selectedMedication =
                                null

                            editError = ""
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        /*
         * SUSPEND MEDICATION DIALOG
         */
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

                                suspensionReason =
                                    it

                                if (
                                    suspensionError
                                        .isNotEmpty()
                                ) {

                                    suspensionError =
                                        ""
                                }
                            },

                            label = {
                                Text("Reason")
                            },

                            isError =
                                suspensionError
                                    .isNotEmpty(),

                            supportingText = {

                                if (
                                    suspensionError
                                        .isNotEmpty()
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
                                suspensionReason
                                    .trim()

                            if (
                                reason.isBlank()
                            ) {

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

        /*
         * RESUME MEDICATION DIALOG
         */
        if (showResumeDialog) {

            AlertDialog(
                onDismissRequest = {

                    showResumeDialog =
                        false

                    selectedSuspensionId =
                        null

                    selectedMedication =
                        null
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

                            showResumeDialog =
                                false

                            selectedSuspensionId =
                                null

                            selectedMedication =
                                null
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

            color =
                statusColor,

            style =
                MaterialTheme
                    .typography
                    .labelMedium,

            modifier =
                Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 6.dp
                )
        )
    }
}
