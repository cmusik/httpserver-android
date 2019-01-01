package de.c11k.httpserver

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        restart_service.visibility = Button.GONE

        updateText()
        startServer()
    }

    private fun checkServiceState() {
        doAsync {
            Thread.sleep(1000)
            uiThread {
                Log.d("MainActivity", "running: "+isServiceRunning(BackgroundService::class.java))
                if (!isServiceRunning(BackgroundService::class.java)) {
                    restart_service.visibility = Button.VISIBLE
                    restart_service.setOnClickListener {
                        stopServer()
                        startServer()
                        restart_service.visibility = Button.GONE
                    }
                }
                else {
                    restart_service.visibility = Button.GONE
                }
            }
        }
    }

    private fun updateText() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val port : String? = prefs.getString("port", "8080")

        var text = getString(R.string.helptext)
        text = text.replace("\$port", port!!.toString())

        helptext.text = text
    }

    private fun startServer() {
        val intent = Intent(this, BackgroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        }
        else {
            startService(intent)
        }
        checkServiceState()
    }

    private fun stopServer() {
        val intent = Intent(this, BackgroundService::class.java)
        stopService(intent)
    }

    @Suppress("DEPRECATION")
    fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
        return (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(Integer.MAX_VALUE)
            .any { it -> it.service.className == service.name }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            updateText()
            stopServer()
            Log.d("MainActivity", "running: "+isServiceRunning(BackgroundService::class.java))
            startServer()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        startActivityForResult(Intent(this, SettingsActivity::class.java), 1)

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
