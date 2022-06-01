package com.t3h.demomediaplayer.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

//service la doi tuong singlton
class CalculationService : Service() {
    companion object {
        private val TAG = CalculationService::class.java.simpleName
    }

    override fun onBind(intent: Intent): IBinder {
        return MyBinder(this)
    }

    class MyBinder(val service: CalculationService) : Binder(){

    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate.....")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand....")
        //loai 1: kill ung dung thi service se chet = START_NOT_STICKY
        //loai 2: kill lan 1, service song la, kill thi service chet =  START_REDELIVER_INTENT
        //loai 3: kill song lai, kill nua, chet di song lai = START_STICKY
        if (intent != null) {
            calculate(intent)
        }

        return START_STICKY;
    }

    private fun calculate(intent: Intent) {
        when (intent.action) {
            "SUM" -> {
                val one = intent.getIntExtra("ONE", 0)
                val two = intent.getIntExtra("TWO", 0)
                val result = one+two
                val intentBroadcast = Intent()
                intentBroadcast.setAction("RESULT_CALCULATION")
                intentBroadcast.putExtra("RESULT", result)
                //gui du lieu di
                sendBroadcast(intentBroadcast)
            }
            "SUB" -> {
                val one = intent.getIntExtra("ONE", 0)
                val two = intent.getIntExtra("TWO", 0)
                val result = one-two

                val intentBroadcast = Intent()
                intentBroadcast.setAction("RESULT_CALCULATION")
                intentBroadcast.putExtra("RESULT", result)
                //gui du lieu di
                sendBroadcast(intentBroadcast)
            }
        }
    }

    fun sum(one:Int, two: Int) : Int{
        return one+two
    }
    fun sub(one:Int, two: Int) : Int{
        return one-two
    }

}