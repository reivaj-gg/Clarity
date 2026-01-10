package com.reivaj.clarity.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinInject()
) {
    val isSeeding by viewModel.isSeeding.collectAsState()
    val message by viewModel.message.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Profile & Settings", style = MaterialTheme.typography.headlineMedium)
            
            Spacer(Modifier.height(32.dp))
            
            Text("User: Guest", style = MaterialTheme.typography.titleMedium)
            Text("Version: 1.0.0 (Contest Build)", style = MaterialTheme.typography.bodySmall)
            
            Spacer(Modifier.height(64.dp))
            
            // Demo Section
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Contest Judge Utilities", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Generate 14 days of synthetic history to test 'Insights' feature immediately.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    
                    if (isSeeding) {
                        CircularProgressIndicator()
                    } else {
                        Button(onClick = viewModel::seedData) {
                            Text("Inject Demo Data")
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            message?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
                LaunchedEffect(it) {
                    kotlinx.coroutines.delay(3000)
                    viewModel.clearMessage()
                }
            }
        }
    }
}

