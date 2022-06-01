package com.t3h.demomediaplayer.permission

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionUtils {
    //kiem tra xem co duoc hien thi thi thong bao cap quyen
    //Neu nguoi dung khong dong y qua 2 lan thi ham nay return false
    fun checkShouldShow(fr: Fragment, permissions: Array<String>) : Boolean{
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(fr.requireContext(), permission) !=
                PackageManager.PERMISSION_GRANTED) {
                    //shouldShowRequestPermissionRationale: check xem co the mo thong bao xin cap quyen
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(fr.requireActivity(),
                        permission)){
                        return false
                    }

            }
        }
        //co the mo thong bao xin cap quyen
        return true
    }

    fun checkOnlyPermission(fr: Fragment, permissions: Array<String>) : Boolean{
        for (permission in permissions) {
            //kiem tra permision nguoi dung da dong y chua
            if (ContextCompat.checkSelfPermission(fr.requireContext(), permission) !=
                PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun checkPermissionAndShowPoup(fr: Fragment, permissions: Array<String>) : Boolean{
        val pers = mutableListOf<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(fr.requireContext(), permission) !=
                PackageManager.PERMISSION_GRANTED) {
                pers.add(permission)
            }
        }
        if ( pers.size == 0){
            return true
        }
        fr.requestPermissions(
            pers.toTypedArray(),
            50)
        return false

    }
}