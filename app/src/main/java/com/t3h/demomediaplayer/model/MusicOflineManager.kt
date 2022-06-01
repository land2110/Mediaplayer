package com.t3h.demomediaplayer.model

import android.media.MediaPlayer

class MusicOflineManager {
    private var mp: MediaPlayer? = null

    constructor()

    fun setPath(path: String) {
        release()
        mp = MediaPlayer()
        mp?.setDataSource(path)
        mp?.prepare()
    }

    fun start() {
        mp?.start()
    }

    fun stop() {
        mp?.stop()
    }

    fun pause() {
        mp?.pause()
    }

    fun release() {
        mp?.release()
        mp = null
    }

    fun getCurrentPosition() : Int{
        if (mp == null ){
            return 0
        }
        return mp!!.currentPosition
    }
    fun getDuration() : Long{
        if (mp == null ){
            return 0
        }
        return mp!!.duration.toLong()
    }

    fun seek(position:Int){
        mp?.seekTo(position)
    }

    fun isEmptyMp() :Boolean{
        return mp == null
    }
}