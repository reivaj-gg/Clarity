package com.reivaj.clarity.util

import platform.Foundation.NSUUID

actual fun randomUUID(): String = NSUUID().UUIDString()
