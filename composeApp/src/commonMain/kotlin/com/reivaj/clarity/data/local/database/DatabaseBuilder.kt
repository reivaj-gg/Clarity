package com.reivaj.clarity.data.local.database

import androidx.room.RoomDatabase

expect fun getDatabaseBuilder(): RoomDatabase.Builder<ClarityDatabase>
