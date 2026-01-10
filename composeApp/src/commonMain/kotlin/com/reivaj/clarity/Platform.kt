package com.reivaj.clarity

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform