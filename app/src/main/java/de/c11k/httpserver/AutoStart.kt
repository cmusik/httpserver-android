package de.c11k.httpserver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager

class AutoStart : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        if (prefs.getBoolean("start_on_boot", true)) {
            context!!.startForegroundService(Intent(context, BackgroundService::class.java))
        }
    }
}