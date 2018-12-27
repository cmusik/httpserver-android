package de.c11k.httpserver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AutoStart : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context!!.startForegroundService(Intent(context, BackgroundService::class.java))
    }
}