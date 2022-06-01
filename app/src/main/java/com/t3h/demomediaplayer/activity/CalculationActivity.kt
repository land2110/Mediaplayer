package com.t3h.demomediaplayer.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.t3h.demomediaplayer.R
import com.t3h.demomediaplayer.databinding.ActivityDemoServiceBinding
import com.t3h.demomediaplayer.service.CalculationService

class CalculationActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDemoServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_demo_service)
        binding.btnSum.setOnClickListener(this)
        binding.btnSub.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_sum -> {
                sum(
                    binding.tvOne.text.toString().toInt(),
                    binding.tvSecond.text.toString().toInt()
                );
            }
            R.id.btn_sub -> {
                sub(
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
    }

    private fun sub(numberOne: Int, numberTwo: Int) {

    }

    private fun updateResult(result: Int) {
        binding.tvResult.setText(result.toString())
    }
}