package com.ntg.movieapiapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val backdropPath: String? = null,
    val title: String,
    val page: Int,
)
