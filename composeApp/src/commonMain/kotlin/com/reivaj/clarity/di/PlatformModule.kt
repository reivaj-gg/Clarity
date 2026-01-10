package com.reivaj.clarity.di

import org.koin.core.module.Module

/**
 * Defines the expected Koin module for platform-specific dependencies.
 *
 * This allows the common module to reference a `platformModule` that will be provided
 * by each specific platform (Android, iOS, etc.), containing dependencies that
 * require platform-specific APIs (e.g., database builders, settings).
 */
expect val platformModule: Module
