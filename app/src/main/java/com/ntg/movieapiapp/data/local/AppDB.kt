package com.ntg.movieapiapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MovieEntity::class], version = 1)
abstract class AppDB: RoomDatabase() {

    abstract val movieDao: MovieDao
}