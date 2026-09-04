package com.refilliq.app

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

data class ScheduleItem(
    val dose: Double,
    val doseUnit: String,
    val time: String
)

@Composable
fun SetMedicationScheduleScreen(
    medicationId: Int,
    medicationName: String,
    strength: String,
    repository: MedicationRepository,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    /*
     * ---------------------------------------------------------
     * FORM STATE
     * ---------------------------------------------------------
     *
     * Dose se mantiene como String en el TextField.
     * Se convierte a Double únicamente al guardar.
     */

    var doseText by remember {
        mutableStateOf("")
    }

    var selectedTime by remember {
        mutableStateOf("")
    }

    var selectedUnit by remember {
        mutableStateOf("tablet")
    }

    var unitMenuExpanded by remember {
        mutableStateOf(false)
    }

    /*
     * Schedules que todavía no se han guardado en Room.
     */
    val pendingSchedules = remember {
        mutableStateListOf<ScheduleItem>()
    }

    /*
     * Schedules existentes en Room.
     */
    val existingSchedules by repository
        .getSchedulesForMedication(medicationId)
        .collectAsState(initial = emptyList())

    /*
     * ---------------------------------------------------------
     * EDIT STATE
     * ---------------------------------------------------------
     */

    var editingScheduleId by remember {
        mutableStateOf<Int?>(null)
    }

    var editingPendingIndex by remember {
        mutableStateOf<Int?>(null)
    }

    var originalTime by remember {
        mutableStateOf("")
    }

    /*
     * ---------------------------------------------------------
     * DIALOGS / MESSAGES
     * ---------------------------------------------------------
     */

    var showDuplicateDialog by remember {
        mutableStateOf(false)
    }

    var takenMessage by remember {
        mutableStateOf("")
    }

    /*
     * ---------------------------------------------------------
     * TAKEN STATE
     * ---------------------------------------------------------
     */

    var takenTodayIds by remember {
        mutableStateOf<Set<Int>>(emptySet())
    }

    /*
     * ---------------------------------------------------------
     * BUTTON STATE
     * ---------------------------------------------------------
     */

    val canSaveSchedule =
        doseText.isNotBlank() &&
                selectedTime.isNotBlank()

    val isEditing =
        editingScheduleId != null ||
                editingPendingIndex != null

    /*
     * ---------------------------------------------------------
     * TODAY RANGE
     * ---------------------------------------------------------
     */

    fun getTodayRange(): Pair<Long, Long> {

        val startCalendar =
            Calendar.getInstance().apply {

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

        val endCalendar =
            startCalendar.clone() as Calendar

        endCalendar.add(
            Calendar.DAY_OF_YEAR,
            1
        )

        return Pair(
            startCalendar.timeInMillis,
            endCalendar.timeInMillis
        )
    }

    /*
     * ---------------------------------------------------------
     * LOAD TAKEN STATUS
     * ---------------------------------------------------------
     */

    LaunchedEffect(existingSchedules) {

        val (startOfDay, endOfDay) =
            getTodayRange()

        val takenIds =
            existingSchedules
                .filter { schedule ->

                    repository.countDoseForScheduleToday(
                        scheduleId = schedule.id,
                        startOfDay = startOfDay,
                        endOfDay = endOfDay
                    ) > 0
                }
                .map { schedule ->
                    schedule.id
                }
                .toSet()

        takenTodayIds = takenIds
    }

    /*
     * ---------------------------------------------------------
     * TIME PICKER
     * ---------------------------------------------------------
     */

    fun showTimePicker(context: Context) {

        val calendar =
            Calendar.getInstance()

        val hour =
            calendar.get(Calendar.HOUR_OF_DAY)

        val minute =
            calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->

                selectedTime =
                    String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        selectedHour,
                        selectedMinute
                    )

            },
            hour,
            minute,
            true
        ).show()
    }

    /*
     * ---------------------------------------------------------
     * CLEAR FORM
     * ---------------------------------------------------------
     */

    fun clearForm() {

        doseText = ""
        selectedTime = ""
        selectedUnit = "tablet"

        editingScheduleId = null
        editingPendingIndex = null
        originalTime = ""

        takenMessage = ""
    }

    /*
     * ---------------------------------------------------------
     * DUPLICATE SCHEDULE DIALOG
     * ---------------------------------------------------------
     */

    if (showDuplicateDialog) {

        AlertDialog(
            onDismissRequest = {
                showDuplicateDialog = false
            },

            title = {
                Text(
                    "Schedule already exists"
                )
            },

            text = {
                Text(
                    "A schedule already exists for this time."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        showDuplicateDialog = false
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    /*
     * ---------------------------------------------------------
     * SCREEN
     * ---------------------------------------------------------
     */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Set Medication Schedule",
            style =
                MaterialTheme
                    .typography
                    .headlineSmall
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = medicationName,
            style =
                MaterialTheme
                    .typography
                    .titleMedium
        )

        Text(
            text = strength,
            style =
                MaterialTheme
                    .typography
                    .bodyMedium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        /*
         * -----------------------------------------------------
         * DOSE
         * -----------------------------------------------------
         */

        OutlinedTextField(
            value = doseText,

            onValueChange = { newValue ->

                if (
                    newValue.isEmpty() ||
                    newValue.matches(
                        Regex(
                            "^\\d*\\.?\\d*$"
                        )
                    )
                ) {

                    doseText = newValue
                }
            },

            label = {
                Text("Dose")
            },

            placeholder = {
                Text("Example: 1")
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
            modifier = Modifier.height(12.dp)
        )

        /*
         * -----------------------------------------------------
         * UNIT
         * -----------------------------------------------------
         */

        Text(
            text = "Unit",
            style =
                MaterialTheme
                    .typography
                    .labelLarge
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Box(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Button(
                onClick = {
                    unitMenuExpanded = true
                },

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text = selectedUnit
                )
            }

            DropdownMenu(
                expanded =
                    unitMenuExpanded,

                onDismissRequest = {
                    unitMenuExpanded = false
                }
            ) {

                DropdownMenuItem(
                    text = {
                        Text("Tablet")
                    },
                    onClick = {
                        selectedUnit = "tablet"
                        unitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Pill")
                    },
                    onClick = {
                        selectedUnit = "pill"
                        unitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Capsule")
                    },
                    onClick = {
                        selectedUnit = "capsule"
                        unitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Softgel")
                    },
                    onClick = {
                        selectedUnit = "softgel"
                        unitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("mL")
                    },
                    onClick = {
                        selectedUnit = "mL"
                        unitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("tsp")
                    },
                    onClick = {
                        selectedUnit = "tsp"
                        unitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("tbsp")
                    },
                    onClick = {
                        selectedUnit = "tbsp"
                        unitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Drop")
                    },
                    onClick = {
                        selectedUnit = "drop"
                        unitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Puff")
                    },
                    onClick = {
                        selectedUnit = "puff"
                        unitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Injection")
                    },
                    onClick = {
                        selectedUnit = "injection"
                        unitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Packet")
                    },
                    onClick = {
                        selectedUnit = "packet"
                        unitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Suppository")
                    },
                    onClick = {
                        selectedUnit = "suppository"
                        unitMenuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Other")
                    },
                    onClick = {
                        selectedUnit = "other"
                        unitMenuExpanded = false
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        /*
         * -----------------------------------------------------
         * TIME
         * -----------------------------------------------------
         */

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showTimePicker(context)
                }
        ) {

            OutlinedTextField(
                value = selectedTime,

                onValueChange = {},

                readOnly = true,

                enabled = false,

                label = {
                    Text("Time")
                },

                placeholder = {
                    Text("Select time")
                },

                modifier =
                    Modifier.fillMaxWidth()
            )
        }

        if (takenMessage.isNotEmpty()) {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = takenMessage,

                color =
                    MaterialTheme
                        .colorScheme
                        .primary,

                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        /*
         * -----------------------------------------------------
         * ADD / UPDATE BUTTON
         * -----------------------------------------------------
         */

        Button(

            onClick = {

                scope.launch {

                    val selectedTimeValue =
                        selectedTime.trim()

                    val doseValue =
                        doseText
                            .trim()
                            .toDoubleOrNull()

                    /*
                     * Invalid dose.
                     */
                    if (
                        doseValue == null ||
                        doseValue <= 0.0
                    ) {
                        return@launch
                    }

                    val editingExisting =
                        editingScheduleId != null

                    val editingPending =
                        editingPendingIndex != null

                    val timeChanged =
                        selectedTimeValue !=
                                originalTime

                    /*
                     * Check database duplicates.
                     */
                    val alreadyExistsInDatabase =
                        if (
                            editingExisting &&
                            !timeChanged
                        ) {

                            false

                        } else {

                            repository.hasScheduleAtTime(
                                medicationId =
                                    medicationId,

                                time =
                                    selectedTimeValue
                            )
                        }

                    /*
                     * Check pending duplicates.
                     */
                    val alreadyExistsInPending =
                        pendingSchedules.anyIndexed {
                                index,
                                schedule ->

                            val isCurrentPending =
                                editingPending &&
                                        editingPendingIndex ==
                                        index

                            !isCurrentPending &&
                                    schedule.time ==
                                    selectedTimeValue
                        }

                    /*
                     * Duplicate.
                     */
                    if (
                        alreadyExistsInDatabase ||
                        alreadyExistsInPending
                    ) {

                        showDuplicateDialog = true

                        return@launch
                    }

                    /*
                     * -------------------------------------------------
                     * UPDATE EXISTING DATABASE SCHEDULE
                     * -------------------------------------------------
                     */

                    if (
                        editingScheduleId != null
                    ) {

                        val updatedSchedule =
                            MedicationSchedule(

                                id =
                                    editingScheduleId!!,

                                medicationId =
                                    medicationId,

                                dose =
                                    doseValue,

                                doseUnit =
                                    selectedUnit,

                                time =
                                    selectedTimeValue
                            )

                        repository.updateSchedule(
                            updatedSchedule
                        )

                        clearForm()

                        /*
                         * -------------------------------------------------
                         * UPDATE PENDING SCHEDULE
                         * -------------------------------------------------
                         */

                    } else if (
                        editingPendingIndex != null
                    ) {

                        val index =
                            editingPendingIndex!!

                        pendingSchedules[index] =
                            ScheduleItem(

                                dose =
                                    doseValue,

                                doseUnit =
                                    selectedUnit,

                                time =
                                    selectedTimeValue
                            )

                        clearForm()

                        /*
                         * -------------------------------------------------
                         * ADD NEW PENDING SCHEDULE
                         * -------------------------------------------------
                         */

                    } else {

                        pendingSchedules.add(
                            ScheduleItem(

                                dose =
                                    doseValue,

                                doseUnit =
                                    selectedUnit,

                                time =
                                    selectedTimeValue
                            )
                        )

                        clearForm()
                    }
                }
            },

            enabled =
                canSaveSchedule
        ) {

            Text(
                if (isEditing) {
                    "Update schedule"
                } else {
                    "Add schedule"
                }
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        /*
         * ---------------------------------------------------------
         * SCHEDULES
         * ---------------------------------------------------------
         */

        Text(
            text = "Schedules",
            style =
                MaterialTheme
                    .typography
                    .titleMedium
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
        ) {

            /*
             * =====================================================
             * EXISTING SCHEDULES
             * =====================================================
             */

            existingSchedules.forEach { schedule ->

                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 4.dp
                            )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(16.dp)
                    ) {

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
                                        "${schedule.dose} ${schedule.doseUnit}",

                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium
                                )
                            }

                            Row {

                                /*
                                 * -------------------------------------------------
                                 * EDIT EXISTING SCHEDULE
                                 * -------------------------------------------------
                                 */

                                TextButton(

                                    onClick = {

                                        /*
                                         * Explicitly load ALL fields.
                                         *
                                         * Double -> String
                                         */
                                        doseText =
                                            schedule.dose
                                                .toString()

                                        selectedUnit =
                                            schedule.doseUnit

                                        selectedTime =
                                            schedule.time

                                        originalTime =
                                            schedule.time

                                        editingScheduleId =
                                            schedule.id

                                        editingPendingIndex =
                                            null

                                        takenMessage = ""
                                    }
                                ) {

                                    Text("Edit")
                                }

                                /*
                                 * -------------------------------------------------
                                 * DELETE
                                 * -------------------------------------------------
                                 */

                                TextButton(

                                    onClick = {

                                        scope.launch {

                                            repository.deleteSchedule(
                                                scheduleId =
                                                    schedule.id
                                            )

                                            takenTodayIds =
                                                takenTodayIds -
                                                        schedule.id
                                        }
                                    }
                                ) {

                                    Text("Delete")
                                }
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        /*
                         * -------------------------------------------------
                         * TAKEN STATUS
                         * -------------------------------------------------
                         */

                        val isTakenToday =
                            takenTodayIds.contains(
                                schedule.id
                            )

                        /*
                         * -------------------------------------------------
                         * TAKEN BUTTON
                         * -------------------------------------------------
                         */

                        Button(

                            onClick = {

                                scope.launch {

                                    val (
                                        startOfDay,
                                        endOfDay
                                    ) =
                                        getTodayRange()

                                    val alreadyTaken =
                                        repository
                                            .countDoseForScheduleToday(

                                                scheduleId =
                                                    schedule.id,

                                                startOfDay =
                                                    startOfDay,

                                                endOfDay =
                                                    endOfDay
                                            ) > 0

                                    if (alreadyTaken) {

                                        takenTodayIds =
                                            takenTodayIds +
                                                    schedule.id

                                        takenMessage =
                                            "${schedule.time} dose was already marked as taken today."

                                    } else {

                                        repository.insertDose(

                                            MedicationDose(

                                                medicationId =
                                                    medicationId,

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

                                        takenTodayIds =
                                            takenTodayIds +
                                                    schedule.id

                                        takenMessage =
                                            "${schedule.time} dose marked as taken."
                                    }
                                }
                            },

                            enabled =
                                !isTakenToday,

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

            /*
             * =====================================================
             * PENDING SCHEDULES
             * =====================================================
             */

            pendingSchedules.forEachIndexed {
                    index,
                    schedule ->

                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 4.dp
                            )
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),

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
                                    "${schedule.dose} ${schedule.doseUnit}",

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )

                            Text(
                                text = "New",

                                style =
                                    MaterialTheme
                                        .typography
                                        .labelMedium
                            )
                        }

                        /*
                         * EDIT PENDING
                         */

                        TextButton(

                            onClick = {

                                doseText =
                                    schedule.dose
                                        .toString()

                                selectedUnit =
                                    schedule.doseUnit

                                selectedTime =
                                    schedule.time

                                originalTime =
                                    schedule.time

                                editingPendingIndex =
                                    index

                                editingScheduleId =
                                    null

                                takenMessage = ""
                            }
                        ) {

                            Text("Edit")
                        }
                    }
                }
            }
        }

        /*
         * ---------------------------------------------------------
         * BOTTOM BUTTONS
         * ---------------------------------------------------------
         */

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.End
        ) {

            TextButton(
                onClick = {
                    onCancel()
                }
            ) {

                Text("Cancel")
            }

            Spacer(
                modifier =
                    Modifier.padding(
                        horizontal = 4.dp
                    )
            )

            Button(

                onClick = {

                    scope.launch {

                        pendingSchedules.forEach {
                                schedule ->

                            repository.insertSchedule(

                                MedicationSchedule(

                                    medicationId =
                                        medicationId,

                                    dose =
                                        schedule.dose,

                                    doseUnit =
                                        schedule.doseUnit,

                                    time =
                                        schedule.time
                                )
                            )
                        }

                        onSave()
                    }
                },

                enabled =
                    pendingSchedules.isNotEmpty()
            ) {

                Text("Save schedule")
            }
        }
    }
}

private inline fun <T> List<T>.anyIndexed(
    predicate: (index: Int, T) -> Boolean
): Boolean {

    for (index in indices) {

        if (
            predicate(
                index,
                this[index]
            )
        ) {
            return true
        }
    }

    return false
}