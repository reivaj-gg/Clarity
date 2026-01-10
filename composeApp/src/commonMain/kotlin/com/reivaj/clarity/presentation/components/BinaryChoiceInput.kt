package com.reivaj.clarity.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A reusable composable for a binary (Yes/No) choice using a checkbox.
 * This component standardizes the presentation of binary questions, ensuring a consistent
 * user experience for these types of inputs.
 *
 * It consists of a label and a checkbox, neatly aligned in a row.
 *
 * @param label The descriptive text for the choice.
 * @param isSelected The current boolean state of the checkbox (true for checked, false for unchecked).
 * @param onCheckedChange A callback that is invoked with the new state when the user
 *                        clicks the checkbox.
 */
@Composable
fun BinaryChoiceInput(
    label: String,
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label)
        Spacer(modifier = Modifier.width(8.dp))
        Checkbox(
            checked = isSelected,
            onCheckedChange = onCheckedChange,
        )
    }
}
