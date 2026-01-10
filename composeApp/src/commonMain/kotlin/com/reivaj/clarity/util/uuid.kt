package com.reivaj.clarity.util

/**
 * Generates a platform-specific random UUID (Universally Unique Identifier).
 *
 * This `expect` function defines a common API for generating UUIDs, which must be implemented
 * in each platform-specific source set (`actual` implementations).
 */
expect fun randomUUID(): String
