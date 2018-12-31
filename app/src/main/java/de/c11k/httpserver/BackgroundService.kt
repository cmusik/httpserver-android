package de.c11k.httpserver

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.preference.PreferenceManager
import java.net.BindException

class BackgroundService : Service() {
    private var server : Webserver? = null
    private var isRunning = false
    private var port : Int = 8080

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        port = prefs.getString("port", "8080")!!.toInt()

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val notification : Notification.Builder

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("1", getString(R.string.service), NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            notification = Notification.Builder(this, "1")
        }
        else {
            notification = Notification.Builder(this)
        }

        notification.setSmallIcon(R.drawable.ic_stat_name)
        notification.setContentTitle("running")
        notification.setContentText("httpserver is running on port $port in background")
        notification.setShowWhen(true)
        notification.setContentIntent(pendingIntent)

        startForeground(1, notification.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            server = Webserver(this, "127.0.0.1", port)
            try {
                server!!.start(5)
            } catch (e : BindException) {
                Log.d("BackgroundService", "caught exception")
            }
            isRunning = true
        }
        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        server!!.stop()
        isRunning = false
        return super.stopService(name)
    }
}