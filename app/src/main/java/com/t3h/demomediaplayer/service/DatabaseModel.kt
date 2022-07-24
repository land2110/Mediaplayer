package com.t3h.demomediaplayer.service

import com.t3h.demomediaplayer.database.MusicOnlineDao
import javax.inject.Inject

class DatabaseModel {
    val musicOnlineDao: MusicOnlineDao

    @Inject
    constructor(mc: MusicOnlineDao) {
        musicOnlineDao = mc
    }
}