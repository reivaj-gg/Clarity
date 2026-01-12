package com.reivaj.clarity.di

import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.data.repository.RoomClarityRepository
import com.reivaj.clarity.presentation.ema.EMAViewModel
import com.reivaj.clarity.presentation.game.GameViewModel
import org.koin.dsl.module

/**
 * Koin module for the application's core components.
 *
 * This module provides definitions for ViewModels and Repositories,
 * allowing for dependency injection throughout the application.
 */
val appModule = module {
    // Repositories
    // Provides a singleton instance of RoomClarityRepository as the ClarityRepository implementation.
    // The `get()` call resolves the database instance from the platform-specific module.
    single<ClarityRepository> { RoomClarityRepository(get()) }

    // ViewModels
    // Provides a new instance of EMAViewModel each time it's requested.
    // UseCases
    factory { com.reivaj.clarity.domain.usecase.SaveEmaUseCase(get()) }
    factory { com.reivaj.clarity.domain.usecase.SaveGameSessionUseCase(get()) }
    factory { com.reivaj.clarity.domain.usecase.GetLatestEmaUseCase(get()) }

    factory { com.reivaj.clarity.domain.usecase.GetSessionsWithEmaUseCase(get()) }
    factory { com.reivaj.clarity.domain.usecase.IsCheckInCompleteUseCase(get()) }
    factory { com.reivaj.clarity.domain.usecase.GetProfileStatsUseCase(get()) }
    factory { com.reivaj.clarity.domain.usecase.GetLast7DaysStatsUseCase(get()) }
    factory { com.reivaj.clarity.domain.usecase.ExportDataUseCase(get()) }
    factory { com.reivaj.clarity.domain.usecase.CalculateAnalyticsUseCase(get()) }
    
    // PDF Report Use Cases
    factory { com.reivaj.clarity.domain.usecase.PerformanceScoreCalculator() }
    factory { com.reivaj.clarity.domain.usecase.CoachInsightGenerator() }
    factory { com.reivaj.clarity.domain.usecase.BuildPdfReportDataUseCase(get(), get(), get(), get()) }
    factory { com.reivaj.clarity.domain.usecase.GeneratePdfReportUseCase(get(), get()) }

    // Utils
    factory { com.reivaj.clarity.domain.util.DataSeeder(get()) }
    
    // AI Coach
    single { com.reivaj.clarity.data.remote.GeminiAiService() }
    factory { com.reivaj.clarity.domain.usecase.BuildAiContextUseCase(get()) }
    factory { com.reivaj.clarity.domain.usecase.SendChatMessageUseCase(get(), get(), get()) }

    // ViewModels
    // ViewModels
    factory { EMAViewModel(get()) }
    factory { GameViewModel(get(), get()) } // Added SoundManager
    factory { com.reivaj.clarity.presentation.dashboard.DashboardViewModel(get(), get()) }
    factory { com.reivaj.clarity.presentation.train.TrainViewModel(get()) }
    factory { com.reivaj.clarity.presentation.game.PatternGameViewModel(get(), get()) } // Added SoundManager
    factory { com.reivaj.clarity.presentation.game.SimonGameViewModel(get(), get()) } // Added SoundManager
    factory { com.reivaj.clarity.presentation.game.VisualSearchViewModel(get(), get()) } // Added SoundManager
    factory { com.reivaj.clarity.presentation.profile.ProfileViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { com.reivaj.clarity.presentation.coach.CoachViewModel(get(), get()) }
    
    // Insights
    factory { com.reivaj.clarity.domain.usecase.GenerateInsightsUseCase(get(), get(), get()) }
    factory { com.reivaj.clarity.presentation.insights.InsightsViewModel(get()) }
}
