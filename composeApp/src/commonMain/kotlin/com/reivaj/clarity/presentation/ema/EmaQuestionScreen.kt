package com.reivaj.clarity.presentation.ema

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmaQuestionScreen(
    questionNumber: Int,
    totalQuestions: Int,
    questionText: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueLabelLeft: String = "Not at all",
    valueLabelRight: String = "Very much",
    onBack: () -> Unit,
    onNext: () -> Unit,
    isNextEnabled: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress Top Bar
        Text(
            "Question $questionNumber of $totalQuestions",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Question Content
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                questionNumber.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            questionText,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Slider Area
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 1f..5f,
            steps = 3 // 1..2..3..4..5
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(valueLabelLeft, style = MaterialTheme.typography.bodySmall)
            Text(text = value.toInt().toString(), style = MaterialTheme.typography.titleMedium)
            Text(valueLabelRight, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.weight(1f))
        
        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (questionNumber > 1) {
                TextButton(onClick = onBack) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp)) // Placeholder
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onNext,
                enabled = isNextEnabled,
                contentPadding = PaddingValues(horizontal = 32.dp)
            ) {
                Text(if (questionNumber == totalQuestions) "Finish" else "Next")
            }
        }
    }
}
