package com.refilliq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.refilliq.app.ui.theme.RefillIQTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RefillIQTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        var medicationName by remember { mutableStateOf("") }

        Text(text = "Add Medication")

        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = medicationName,
            onValueChange = { medicationName = it },
            label = { Text("Medication Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        var strength by remember { mutableStateOf("") }
        var quantity by remember { mutableStateOf("") }
        var dailyUsage by remember { mutableStateOf("") }
        var savedMessage by remember { mutableStateOf("") }
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
        Text(
            text = savedMessage
        )
    }
}