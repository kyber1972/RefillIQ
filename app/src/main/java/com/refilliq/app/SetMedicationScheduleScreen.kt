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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
    val dose: String,
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

    val dose = remember {
        mutableStateOf("")
    }

    val time = remember {
        mutableStateOf("")
    }

    val selectedUnit = remember {
        mutableStateOf("tablet")
    }

    val unitMenuExpanded = remember {
        mutableStateOf(false)
    }

    val pendingSchedules = remember {
        mutableStateListOf<ScheduleItem>()
    }

    val existingSchedules by repository
        .getSchedulesForMedication(medicationId)
        .collectAsState(initial = emptyList())

    val duplicateError = remember {
        mutableStateOf("")
    }

    val editingScheduleId = remember {
        mutableStateOf<Int?>(null)
    }

    val editingPendingIndex = remember {
        mutableStateOf<Int?>(null)
    }

    val originalTime = remember {
        mutableStateOf("")
    }

    val canAddSchedule =
        dose.value.isNotBlank() &&
                time.value.isNotBlank()

    val isEditing =
        editingScheduleId.value != null ||
                editingPendingIndex.value != null

    fun showTimePicker(context: Context) {

        val calendar = Calendar.getInstance()

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->

                time.value = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    selectedHour,
                    selectedMinute
                )

                duplicateError.value = ""
            },
            hour,
            minute,
            true
        ).show()
    }

    fun clearEditing() {

        editingScheduleId.value = null
        editingPendingIndex.value = null
        originalTime.value = ""

        dose.value = ""
        time.value = ""
        selectedUnit.value = "tablet"
        duplicateError.value = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Set Medication Schedule",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = medicationName,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = strength,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedTextField(
            value = dose.value,
            onValueChange = { newValue ->

                if (
                    newValue.isEmpty() ||
                    newValue.matches(
                        Regex("^\\d*\\.?\\d*$")
                    )
                ) {
                    dose.value = newValue
                    duplicateError.value = ""
                }
            },
            label = {
                Text("Dose")
            },
            placeholder = {
                Text("Example: 1")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = "Unit",
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
                    unitMenuExpanded.value = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedUnit.value
                )
            }

            DropdownMenu(
                expanded = unitMenuExpanded.value,
                onDismissRequest = {
                    unitMenuExpanded.value = false
                }
            ) {

                DropdownMenuItem(
                    text = { Text("Tablet") },
                    onClick = {
                        selectedUnit.value = "tablet"
                        unitMenuExpanded.value = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("Pill") },
                    onClick = {
                        selectedUnit.value = "pill"
                        unitMenuExpanded.value = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("Capsule") },
                    onClick = {
                        selectedUnit.value = "capsule"
                        unitMenuExpanded.value = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("Softgel") },
                    onClick = {
                        selectedUnit.value = "softgel"
                        unitMenuExpanded.value = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("mL") },
                    onClick = {
                        selectedUnit.value = "mL"
                        unitMenuExpanded.value = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("tsp") },
                    onClick = {
                        selectedUnit.value = "tsp"
                        unitMenuExpanded.value = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("tbsp") },
                    onClick = {
                        selectedUnit.value = "tbsp"
                        unitMenuExpanded.value = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("Drop") },
                    onClick = {
                        selectedUnit.value = "drop"
                        unitMenuExpanded.value = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("Puff") },
                    onClick = {
                        selectedUnit.value = "puff"
                        unitMenuExpanded.value = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("Injection") },
                    onClick = {
                        selectedUnit.value = "injection"
                        unitMenuExpanded.value = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("Packet") },
                    onClick = {
                        selectedUnit.value = "packet"
                        unitMenuExpanded.value = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("Suppository") },
                    onClick = {
                        selectedUnit.value = "suppository"
                        unitMenuExpanded.value = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("Other") },
                    onClick = {
                        selectedUnit.value = "other"
                        unitMenuExpanded.value = false
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showTimePicker(context)
                }
        ) {

            OutlinedTextField(
                value = time.value,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = {
                    Text("Time")
                },
                placeholder = {
                    Text("Select time")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (duplicateError.value.isNotEmpty()) {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = duplicateError.value,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = {

                scope.launch {

                    val selectedTime = time.value.trim()

                    val editingExisting =
                        editingScheduleId.value != null

                    val editingPending =
                        editingPendingIndex.value != null

                    val timeChanged =
                        selectedTime != originalTime.value

                    val alreadyExistsInDatabase =
                        if (
                            editingExisting &&
                            !timeChanged
                        ) {
                            false
                        } else {
                            repository.hasScheduleAtTime(
                                medicationId = medicationId,
                                time = selectedTime
                            )
                        }

                    val alreadyExistsInPending =
                        pendingSchedules.anyIndexed { index, schedule ->

                            val isCurrentPending =
                                editingPending &&
                                        editingPendingIndex.value == index

                            !isCurrentPending &&
                                    schedule.time == selectedTime
                        }

                    if (
                        alreadyExistsInDatabase ||
                        alreadyExistsInPending
                    ) {

                        duplicateError.value =
                            "A schedule already exists for this time."

                    } else {

                        if (editingScheduleId.value != null) {

                            val updatedSchedule =
                                MedicationSchedule(
                                    id = editingScheduleId.value!!,
                                    medicationId = medicationId,
                                    dose = dose.value.trim(),
                                    doseUnit = selectedUnit.value,
                                    time = selectedTime
                                )

                            repository.updateSchedule(
                                updatedSchedule
                            )

                        } else if (
                            editingPendingIndex.value != null
                        ) {

                            val index =
                                editingPendingIndex.value!!

                            pendingSchedules[index] =
                                ScheduleItem(
                                    dose = dose.value.trim(),
                                    doseUnit = selectedUnit.value,
                                    time = selectedTime
                                )

                        } else {

                            pendingSchedules.add(
                                ScheduleItem(
                                    dose = dose.value.trim(),
                                    doseUnit = selectedUnit.value,
                                    time = selectedTime
                                )
                            )
                        }

                        clearEditing()
                    }
                }
            },
            enabled = canAddSchedule
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

        Text(
            text = "Schedules",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            existingSchedules.forEach { schedule ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Column {

                            Text(
                                text = schedule.time,
                                style =
                                    MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text =
                                    "${schedule.dose} ${schedule.doseUnit}",
                                style =
                                    MaterialTheme.typography.bodyMedium
                            )
                        }

                        Row {

                            TextButton(
                                onClick = {

                                    dose.value =
                                        schedule.dose

                                    selectedUnit.value =
                                        schedule.doseUnit

                                    time.value =
                                        schedule.time

                                    originalTime.value =
                                        schedule.time

                                    editingScheduleId.value =
                                        schedule.id

                                    editingPendingIndex.value =
                                        null

                                    duplicateError.value = ""
                                }
                            ) {
                                Text("Edit")
                            }

                            TextButton(
                                onClick = {

                                    scope.launch {

                                        repository.deleteSchedule(
                                            scheduleId = schedule.id
                                        )
                                    }
                                }
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }

            pendingSchedules.forEachIndexed { index, schedule ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Column {

                            Text(
                                text = schedule.time,
                                style =
                                    MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text =
                                    "${schedule.dose} ${schedule.doseUnit}",
                                style =
                                    MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "New",
                                style =
                                    MaterialTheme.typography.labelMedium
                            )
                        }

                        TextButton(
                            onClick = {

                                dose.value =
                                    schedule.dose

                                selectedUnit.value =
                                    schedule.doseUnit

                                time.value =
                                    schedule.time

                                originalTime.value =
                                    schedule.time

                                editingPendingIndex.value =
                                    index

                                editingScheduleId.value =
                                    null

                                duplicateError.value = ""
                            }
                        ) {
                            Text("Edit")
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
                onClick = {
                    onCancel()
                }
            ) {
                Text("Cancel")
            }

            Spacer(
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Button(
                onClick = {

                    scope.launch {

                        pendingSchedules.forEach { schedule ->

                            repository.insertSchedule(
                                MedicationSchedule(
                                    medicationId = medicationId,
                                    dose = schedule.dose,
                                    doseUnit = schedule.doseUnit,
                                    time = schedule.time
                                )
                            )
                        }

                        onSave()
                    }
                },
                enabled = pendingSchedules.isNotEmpty()
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

        if (predicate(index, this[index])) {
            return true
        }
    }

    return false
}