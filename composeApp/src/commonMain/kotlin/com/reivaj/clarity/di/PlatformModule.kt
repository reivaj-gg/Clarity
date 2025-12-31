package com.reivaj.clarity.di

import org.koin.core.module.Module

/**
 * Expected declaration for platform-specific Koin modules.
 * Each platform (Android, iOS, etc.) will provide an 'actual'
 * implementation for this module, defining platform-specific dependencies.
 */
expect val platformModule: Module
