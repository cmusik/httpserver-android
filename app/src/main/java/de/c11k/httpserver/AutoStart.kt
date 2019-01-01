package de.c11k.httpserver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.preference.PreferenceManager

class AutoStart : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val serviceIntent = Intent(context, BackgroundService::class.java)
            if (prefs.getBoolean("start_on_boot", true)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context!!.startForegroundService(serviceIntent)
                }
                else {
                    context!!.startService(serviceIntent)
                }
            }
        }
    }
}