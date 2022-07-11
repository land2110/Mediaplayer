package com.t3h.demomediaplayer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.t3h.demomediaplayer.model.MusicOnline

@Dao
interface MusicOnlineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertchAll(musics: MutableList<MusicOnline>)

    @Query("SELECT * FROM music_online WHERE id = :id")
    fun getAllMusicById(id: String): MusicOnline

    @Query("SELECT * FROM music_online WHERE keySearch = :keySearch")
    fun getAllMusicByKeySearch(keySearch: String): MutableList<MusicOnline>

    @Query("DELETE FROM music_online WHERE keySearch = :keySearch")
    fun delete(keySearch: String)
}