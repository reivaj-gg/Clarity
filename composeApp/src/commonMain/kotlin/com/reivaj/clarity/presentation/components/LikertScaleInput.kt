package com.reivaj.clarity.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

/**
 * A reusable composable for a numeric input field.
 * This component now correctly handles Float values, preventing crashes.
 *
 * @param label The text to display as the label for the input field.
 * @param value The current Float value of the input field.
 * @param onValueChange A callback that is invoked when the user enters text.
 */
@Composable
fun LikertScaleInput(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Column {
        OutlinedTextField(
            value = value.toString(),
            onValueChange = {
                // Allow clearing the field, but otherwise only accept valid floats
                onValueChange(it.toFloatOrNull() ?: 0f)
            },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
        )
    }
}
