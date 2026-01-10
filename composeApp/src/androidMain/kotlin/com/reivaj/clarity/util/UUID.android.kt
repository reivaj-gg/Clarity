package com.reivaj.clarity.util

import java.util.UUID

actual fun randomUUID(): String = UUID.randomUUID().toString()
