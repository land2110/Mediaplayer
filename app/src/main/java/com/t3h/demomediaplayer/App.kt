package com.t3h.demomediaplayer

import android.app.Application
import com.t3h.demomediaplayer.database.AppDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.getInstance(this)
    }
}