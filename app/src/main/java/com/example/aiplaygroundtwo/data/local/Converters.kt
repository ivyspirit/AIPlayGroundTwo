package com.example.aiplaygroundtwo.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString(separator = LIST_SEPARATOR)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split(LIST_SEPARATOR).orEmpty().filter { it.isNotEmpty() }.ifEmpty { null }
    }

    private companion object {
        const val LIST_SEPARATOR = "\u001F"
    }
}
