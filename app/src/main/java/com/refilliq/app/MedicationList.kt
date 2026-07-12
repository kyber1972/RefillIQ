package com.refilliq.app

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable

@Composable
fun MedicationList(
    medications: List<Medication>
) {

    LazyColumn {

        items(medications) { medication ->

            MedicationCard(
                medication = medication
            )

        }

    }

}