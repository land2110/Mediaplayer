package com.t3h.demomediaplayer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.t3h.demomediaplayer.model.MusicOnline

@Database(entities = [MusicOnline::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun musicOnlineDao(): MusicOnlineDao

    companion object {
        private fun buildDatabase(context: Context) =
//            AppDatabase_Impl
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app.db")
                .allowMainThreadQueries()
                .build()

        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = buildDatabase(context)
            }
            return INSTANCE!!
        }
    }

}