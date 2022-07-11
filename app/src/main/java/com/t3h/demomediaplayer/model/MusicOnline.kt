package com.t3h.demomediaplayer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_online")
data class MusicOnline(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "keySearch") val keySearch: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "artis") val artis: String?,
    @ColumnInfo(name = "htmlLink") val htmlLink: String,
    @ColumnInfo(name = "imageLink") val imageLink: String
) {
}