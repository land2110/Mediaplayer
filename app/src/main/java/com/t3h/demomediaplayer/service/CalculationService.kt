package com.t3h.demomediaplayer.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
//service la doi tuong singlton
class CalculationService : Service() {
    companion object{
        private val TAG = CalculationService::class.java.simpleName
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate.....")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //loai 1: kill ung dung thi service se chet = START_NOT_STICKY
        //loai 2: kill lan 1, service song la, kill thi service chet =  START_REDELIVER_INTENT
        //loai 3: kill song lai, kill nua, chet di song lai = START_STICKY
        return START_STICKY;
    }

}