package com.reivaj.clarity.di

import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.data.repository.InMemoryClarityRepository
import com.reivaj.clarity.presentation.ema.EMAViewModel
import com.reivaj.clarity.presentation.train.TrainViewModel
import com.reivaj.clarity.presentation.game.PatternGameViewModel
import com.reivaj.clarity.presentation.game.SimonGameViewModel
import com.reivaj.clarity.presentation.game.VisualSearchViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

import com.reivaj.clarity.presentation.dashboard.DashboardViewModel
import com.reivaj.clarity.presentation.game.GameViewModel

val appModule = module {
    singleOf(::InMemoryClarityRepository) bind ClarityRepository::class
    factoryOf(::EMAViewModel)
    factoryOf(::GameViewModel)
    factoryOf(::DashboardViewModel)
    factoryOf(::TrainViewModel)
    factoryOf(::PatternGameViewModel)
    factoryOf(::SimonGameViewModel)
    factoryOf(::VisualSearchViewModel)
}
