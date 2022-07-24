package com.t3h.demomediaplayer.di.module

import android.content.Context
import com.t3h.demomediaplayer.common.App
import com.t3h.demomediaplayer.database.AppDatabase
import com.t3h.demomediaplayer.database.MusicOnlineDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class MediaPlayerMusicOnlineModel(val context: Context) {
    @Provides
    fun providedContext(): Context {
        return context
    }

    @Singleton
    @Provides
    fun providedMusicOnlineDao(context:Context): MusicOnlineDao {
        return AppDatabase.getInstance().musicOnlineDao()
    }

}