package com.t3h.demomediaplayer.model

import java.util.*

data class MusicOffline(val id:Int, val path: String, val title:String, var duration:Long, val artist:String, val albumId:Long)