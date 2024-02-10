package com.ntg.movieapiapp.data.local

import androidx.room.TypeConverter

class MovieConvertor {

    @TypeConverter
    fun toString(value: List<String>?): String? {
        return value?.joinToString(separator = ",") { it }
    }

    @TypeConverter
    fun toList(date: String?): List<String>? {
        return date?.split(",")?.map { it }
    }
}