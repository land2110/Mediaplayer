package com.t3h.demomediaplayer.activity

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.t3h.demomediaplayer.R
import com.t3h.demomediaplayer.databinding.ActivityDemoServiceBinding
import com.t3h.demomediaplayer.service.CalculationService

class CalculationActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDemoServiceBinding
    private var service: CalculationService?=null
    private var conn:ServiceConnection?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_demo_service)
        binding.btnSum.setOnClickListener(this)
        binding.btnSub.setOnClickListener(this)
        //dung cho service Unbound
        registerBroadcast()

        //service Bound
        startConnectByConnectionService()
    }

    private fun registerBroadcast(){
        val br = CalculatorBroadcast(binding.tvResult)
        //Intent filter de dang ky
        val filter = IntentFilter()
        //dang ky action cua broadcast
        filter.addAction("RESULT_CALCULATION")
        registerReceiver(br, filter)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_sum -> {
//                sum(
//                    binding.tvOne.text.toString().toInt(),
//                    binding.tvSecond.text.toString().toInt()
//                );
                sumBound(
                    binding.tvOne.text.toString().toInt(),
                    binding.tvSecond.text.toString().toInt()
                );
            }
            R.id.btn_sub -> {
//                sub(
//                    binding.tvOne.text.toString().toInt(),
//                    binding.tvSecond.text.toString().toInt()
//                );
                subBound(
                    binding.tvOne.text.toString().toInt(),
                    binding.tvSecond.text.toString().toInt()
                );
            }
        }
    }

    private fun sum(numberOne: Int, numberTwo: Int) {
        val intentService = Intent()
//        intentService.setClassName("com.t3h.demomediaplayer", "com.t3h.demomediaplayer.service.CalculationService")
        intentService.setClass(this, CalculationService::class.java)
        intentService.setAction("SUM")
        intentService.putExtra("ONE", numberOne)
        intentService.putExtra("TWO", numberTwo)
        startService(intentService)
    }

    private fun sub(numberOne: Int, numberTwo: Int) {
        val intentService = Intent()
        intentService.setClass(this, CalculationService::class.java)
        intentService.setAction("SUB")
        intentService.putExtra("ONE", numberOne)
        intentService.putExtra("TWO", numberTwo)
        startService(intentService)
    }
    private fun sumBound(numberOne: Int, numberTwo: Int) {
        if (service != null){
            val result = service!!.sum(numberOne, numberTwo);
            binding.tvResult.setText(result.toString());
        }
    }

    private fun subBound(numberOne: Int, numberTwo: Int) {
        if (service != null){
            val result = service!!.sub(numberOne, numberTwo);
            binding.tvResult.setText(result.toString());
        }
    }

    private fun updateResult(result: Int) {
        binding.tvResult.setText(result.toString())
    }

    //dinh nghia
    internal class CalculatorBroadcast(val tv: TextView) : BroadcastReceiver(){
        //noi nhan du lieu
        override fun onReceive(context: Context, intent: Intent) {
            val result = intent.getIntExtra("RESULT", 0)
            tv.setText(result.toString())
        }
    }

    private fun startConnectByConnectionService(){
        conn = object:ServiceConnection{
            override fun onServiceConnected(cop: ComponentName, binber: IBinder) {
//                Lay ra servcie trong IBinder
                service = (binber as CalculationService.MyBinder).service;
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                service = null
            }
        }

        val intent = Intent()
        intent.setClass(this, CalculationService::class.java)
        bindService(intent, conn!!, BIND_AUTO_CREATE)

    }

    override fun onDestroy() {
        if (conn != null){
            unbindService(conn!!)
        }

        super.onDestroy()
    }


}