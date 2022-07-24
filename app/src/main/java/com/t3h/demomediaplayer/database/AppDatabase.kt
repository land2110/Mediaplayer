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
        fun inits(context: Context){
            INSTANCE = buildDatabase(context)
        }
        fun getInstance(): AppDatabase {
            return INSTANCE!!
        }
    }

}