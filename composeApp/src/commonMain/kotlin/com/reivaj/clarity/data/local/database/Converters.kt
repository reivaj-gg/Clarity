package com.reivaj.clarity.data.local.database

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

// Although we stored timestamps as Long in Entity to be safe/explicit, 
// using TypeConverters allows us to use Instant directly if we wanted.
// For now, we manually mapped them in Entity to Long. 
// But if we decide to use Instant in Entity later, we use this.
//
// Actually, for Room KMP alpha, explicit primitives are safest. 
// We will simply putting this here as a placeholder or for future complex types.

class Converters {
    // Placeholder: If we used Instant in Entity
    // @TypeConverter
    // fun fromTimestamp(value: Long?): Instant? = value?.let { Instant.fromEpochMilliseconds(it) }

    // @TypeConverter
    // fun dateToTimestamp(date: Instant?): Long? = date?.toEpochMilliseconds()
}
