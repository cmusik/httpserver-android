package de.c11k.httpserver

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder

class BackgroundService : Service() {
    private var server : Webserver? = null
    private var isRunning = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val channel = NotificationChannel("1", "main", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notification: Notification.Builder = Notification.Builder(this)
        notification.setSmallIcon(R.mipmap.ic_launcher)
        notification.setContentTitle("running")
        notification.setContentText("httpserver is running in background")
        notification.setShowWhen(true)
        notification.setContentIntent(pendingIntent)
        notification.setChannelId("1")

        startForeground(1, notification.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            server = Webserver(this, "127.0.0.1", 8080)
            server!!.start()
            isRunning = true
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun stopService(name: Intent?): Boolean {
        server!!.stop()
        isRunning = false
        return super.stopService(name)
    }
}