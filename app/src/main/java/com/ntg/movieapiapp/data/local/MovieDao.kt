package com.ntg.movieapiapp.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface MovieDao {

    @Upsert
    suspend fun upsertAll(movies: List<MovieEntity>)

    @Query("SELECT * FROM MovieEntity")
    fun pagingSource(): PagingSource<Int, MovieEntity>

    @Query("DELETE FROM MovieEntity")
    fun clear()


}