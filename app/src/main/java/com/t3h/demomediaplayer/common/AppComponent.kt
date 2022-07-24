package com.t3h.demomediaplayer.common

import com.t3h.demomediaplayer.MainActivity
import com.t3h.demomediaplayer.di.module.MediaPlayerMusicOnlineModel
import com.t3h.demomediaplayer.di.module.TestModule
import com.t3h.demomediaplayer.service.MediaPlayerMusicOnlineService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MediaPlayerMusicOnlineModel::class, TestModule::class])
interface AppComponent {
    //lat nua giai thich
    fun inject(service: MediaPlayerMusicOnlineService)
    fun inject(activity: MainActivity)
}