package com.reivaj.clarity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// Explicit import to avoid ambiguity
import androidx.compose.material3.Icon
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

import com.reivaj.clarity.theme.ClarityTheme

/**
 * The main entry point for the shared Compose Multiplatform UI.
 *
 * This Composable sets up:
 * 1. The [ClarityTheme] (Material 3).
 * 2. The Koin dependency injection context.
 * 3. The top-level [NavHost] and [Scaffold] structure.
 */
@Composable
fun App() {
    ClarityTheme {
        // Koin is initialized in AndroidApp on Android.
        // koinInject will use GlobalContext.
        // Wrap in KoinContext to silence warning and ensure Compose scope interaction
        KoinContext {
            val navController = rememberNavController()
            // Track current route for Bottom Bar selection
            // In complex apps, use `navController.currentBackStackEntryAsState()`
            // For simplicity in MVP:
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = false, // To implement: observe current route
                            onClick = { navController.navigate("train") { launchSingleTop = true } },
                            icon = { Icon(Icons.Default.FitnessCenter, "Train") },
                            label = { Text("Train") }
                        )
                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate("insights") { launchSingleTop = true } },
                            icon = { Icon(Icons.Default.Lightbulb, "Insights") },
                            label = { Text("Insights") }
                        )
                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate("profile") { launchSingleTop = true } },
                            icon = { Icon(Icons.Default.Person, "Profile") },
                            label = { Text("Profile") }
                        )
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    NavHost(
                        navController = navController,
                        startDestination = "train"
                    ) {
                        composable("checkin") {
                            com.reivaj.clarity.presentation.ema.EMAScreen(
                                onNavigateToGames = {
                                    navController.navigate("train") {
                                        popUpTo("checkin") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("train") {
                            com.reivaj.clarity.presentation.train.TrainScreen(
                                onNavigateToGame = { gameId ->
                                    navController.navigate("game/$gameId")
                                },
                                onNavigateToCheckIn = {
                                    navController.navigate("checkin")
                                }
                            )
                        }

                        composable("insights") {
                            com.reivaj.clarity.presentation.insights.InsightsScreen()
                        }

                        composable("profile") {
                            com.reivaj.clarity.presentation.profile.ProfileScreen()
                        }

                        // Games
                        composable("game/gonogo") {
                            com.reivaj.clarity.presentation.game.GoNoGoGameScreen(
                                onNavigateHome = { navController.navigate("train") }
                            )
                        }

                        composable("game/pattern") {
                            com.reivaj.clarity.presentation.game.PatternGameScreen(
                                onNavigateHome = { navController.navigate("train") }
                            )
                        }

                        composable("game/simon") {
                            com.reivaj.clarity.presentation.game.SimonGameScreen(
                                onNavigateHome = { navController.navigate("train") }
                            )
                        }

                        composable("game/search") {
                            com.reivaj.clarity.presentation.game.VisualSearchScreen(
                                onNavigateHome = { navController.navigate("train") }
                            )
                        }

                        // Fallback for other games not implemented
                        composable("game/{gameId}") { backStackEntry ->
                            val gameId = backStackEntry.arguments?.getString("gameId")
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Game $gameId coming soon!")
                            }
                        }

                        // EMA (If needed to be accessed separately or as a wizard,
                        // currently reachable from Train if we add a check-in button there,
                        // or we can make it the startDestination check.)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}
