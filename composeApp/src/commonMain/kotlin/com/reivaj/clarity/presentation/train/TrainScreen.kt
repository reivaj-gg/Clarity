package com.reivaj.clarity.presentation.train

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import clarity.composeapp.generated.resources.Res
import clarity.composeapp.generated.resources.logoClarity
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale

data class GameCardData(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

@Composable
fun TrainScreen(
    onNavigateToGame: (String) -> Unit,
    onNavigateToCheckIn: () -> Unit
) {
    val viewModel = koinInject<TrainViewModel>()
    val isCheckInCompleted by viewModel.isCheckInCompleted.collectAsState()

    val games = listOf(
        GameCardData("gonogo", "Go/No-Go", "Train your response inhibition", Icons.Default.Timer),
        GameCardData("pattern", "Pattern Grid", "Practice visuospatial memory", Icons.Default.GridView),
        GameCardData("simon", "Simon Sequence", "Remember growing sequences", Icons.Default.LinearScale),
        GameCardData("search", "Visual Search", "Find targets among distractors", Icons.Default.Search)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with Welcome & Logo
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Welcome to Clarity",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Building Minds",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(Res.drawable.logoClarity),
                contentDescription = "Clarity Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
        }

        // Daily Check-in Card (Always available)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToCheckIn() }, // Special route for checkin
            colors = CardDefaults.cardColors(
                containerColor = if (isCheckInCompleted) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
            )
        ) {
             Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                 Icon(Icons.Default.Edit, "Check-in")
                 Spacer(modifier = Modifier.width(16.dp))
                 Column {
                     Text("Daily Check-in", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                     Text(
                         if (isCheckInCompleted) "Completed for today" else "Required to unlock games",
                         style = MaterialTheme.typography.bodyMedium
                     )
                 }
                 Spacer(modifier = Modifier.weight(1f))
                 if (isCheckInCompleted) {
                     Icon(Icons.Default.CheckCircle, "Done", tint = MaterialTheme.colorScheme.primary)
                 } else {
                     Icon(Icons.Default.ArrowForward, "Go")
                 }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Cognitive Training", style = MaterialTheme.typography.titleLarge, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(games) { game ->
                GameCard(
                    game = game, 
                    onClick = { onNavigateToGame(game.id) },
                    enabled = isCheckInCompleted
                )
            }
        }
    }
}

@Composable
fun GameCard(
    game: GameCardData,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) 
                             else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = game.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (enabled) Color.Unspecified else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = game.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
            
            if (!enabled) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Go",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
