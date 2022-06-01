package com.t3h.demomediaplayer.model

import android.media.MediaPlayer
import android.util.Log

class MusicManager : MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnCompletionListener {

    companion object{
        //static
        private var instance: MusicManager?=null
        fun getInstance(): MusicManager{
            if (instance == null){
                instance = MusicManager()
            }
            return instance!!
        }
    }

    private var mp: MediaPlayer? = null
    private var isPrepared = false
    private constructor()


    fun setUrl(url: String) {
        release()
        mp = MediaPlayer()
        mp?.setOnPreparedListener(this)
        mp?.setDataSource(url)
        isPrepared = false
        mp?.setOnBufferingUpdateListener(this)
        mp?.setOnCompletionListener(this)
        if ( url.contains("http")){
            mp?.prepareAsync()
        }else {
            mp?.prepare()
            start()
            isPrepared=true
        }

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

    fun seek(position:Int){
        mp?.seekTo(position)
    }

    fun isEmptyMp() :Boolean{
        return mp == null
    }

    override fun onPrepared(mp: MediaPlayer) {
        isPrepared = true
        start()
    }

    fun getDuration(): Long{
        if (mp != null && isPrepared){
            return mp!!.duration.toLong()
        }else {
            return 0
        }

    }

    override fun onBufferingUpdate(mp: MediaPlayer, percent: Int) {
        Log.d("MusicOnlineManager", "onBufferingUpdate: "+percent.toString())
    }

    override fun onCompletion(mp: MediaPlayer) {
        Log.d("MusicOnlineManager", "onCompletion: ")
    }
}