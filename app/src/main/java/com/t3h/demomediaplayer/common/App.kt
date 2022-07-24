package com.t3h.demomediaplayer.common

import android.app.Application
import android.content.Context
import com.t3h.demomediaplayer.database.AppDatabase
import com.t3h.demomediaplayer.di.module.MediaPlayerMusicOnlineModel
import com.t3h.demomediaplayer.di.module.TestModule

class App : Application() {
    companion object {
        private lateinit var context: Context
        fun getContext(): Context {
            return context
        }
    }

    private lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
        context = this
        AppDatabase.inits(this)
        appComponent = DaggerAppComponent.builder()
            .mediaPlayerMusicOnlineModel(
                MediaPlayerMusicOnlineModel(this)
            )
            .testModule(TestModule())
            .build()
    }

    fun appComponent(): AppComponent {
        return appComponent
    }
}