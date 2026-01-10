package com.reivaj.clarity.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.reivaj.clarity.presentation.components.BinaryChoiceInput
import com.reivaj.clarity.presentation.components.LikertScaleInput
import com.reivaj.clarity.presentation.viewmodel.AssessmentViewModel

/**
 * A Composable screen that allows users to submit a daily Ecological Momentary Assessment (EMA).
 * This screen is the primary user interface for the assessment feature.
 */
@Composable
fun AssessmentScreen(viewModel: AssessmentViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // Correctly use the padding values provided by the Scaffold.
    // Ignoring these padding values can cause runtime crashes during the layout phase.
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Apply the Scaffold's padding first.
                .padding(16.dp),      // Then apply any additional custom padding.
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Daily Context Assessment", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(24.dp))

            uiState.validationError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            LikertScaleInput(
                label = "Mood (1=Very Sad, 10=Very Happy)",
                value = uiState.mood,
                onValueChange = viewModel::onMoodChanged,
            )

            Spacer(modifier = Modifier.height(16.dp))

            LikertScaleInput(
                label = "Stress (1=Not Stressed, 10=Very Stressed)",
                value = uiState.stress,
                onValueChange = viewModel::onStressChanged,
            )

            Spacer(modifier = Modifier.height(16.dp))

            LikertScaleInput(
                label = "Sleep Quality (1=Poor, 10=Excellent)",
                value = uiState.sleepQuality,
                onValueChange = viewModel::onSleepQualityChanged,
            )

            Spacer(modifier = Modifier.height(16.dp))

            LikertScaleInput(
                label = "Hours Slept",
                value = uiState.hoursSlept,
                onValueChange = viewModel::onHoursSleptChanged,
            )

            Spacer(modifier = Modifier.height(16.dp))

            BinaryChoiceInput(
                label = "Did you experience a significant negative event recently?",
                isSelected = uiState.hasRecentNegativeEvent,
                onCheckedChange = viewModel::onHasRecentNegativeEventChanged,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewModel::submitAssessment,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Submit Assessment")
            }
        }
    }
}
