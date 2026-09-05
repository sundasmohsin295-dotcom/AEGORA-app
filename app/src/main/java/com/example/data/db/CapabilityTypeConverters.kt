package com.example.data.db

import androidx.room.TypeConverter

/**
 * Type converters for Room database persistence of collections and custom types.
 */
class CapabilityTypeConverters {

  @TypeConverter
  fun fromStringList(value: List<String>?): String {
    if (value.isNullOrEmpty()) return ""
    return value.joinToString(separator = "|||")
  }

  @TypeConverter
  fun toStringList(value: String?): List<String> {
    if (value.isNullOrBlank()) return emptyList()
    return value.split("|||").filter { it.isNotBlank() }
  }
}
