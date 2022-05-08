package com.example.networktest

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

internal fun Activity.showSnackbar(view: View, message: String, noConnection: Boolean = false, duration:Int = 0 ) {
    val sb = Snackbar.make(view,message, duration)

    if (noConnection){
        sb.setBackgroundTint(loadColor(R.color.red))
            .setTextColor(loadColor(R.color.white))
            .show()
    } else{
        sb.setBackgroundTint(loadColor(R.color.green))
            .setTextColor(loadColor(R.color.white))
            .show()
    }

    if (duration == -2){
        Handler(Looper.getMainLooper()).postDelayed({
            sb.dismiss()
        }, 4000)
    }

}

internal fun Context.loadColor(@ColorRes colorRes: Int):Int{
    return ContextCompat.getColor(this,colorRes)
}

internal fun Context.showToast(message: String) {
    Toast.makeText(this, "Connection type:$message", Toast.LENGTH_LONG).show()
}