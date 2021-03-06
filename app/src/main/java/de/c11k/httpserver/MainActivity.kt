package de.c11k.httpserver

import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
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

    private fun copyToClipboard(str : String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.primaryClip = ClipData.newPlainText(str, str)
        Toast.makeText(this, getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show()
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

        copy_intent.setOnClickListener {
            copyToClipboard("de.c11k.httpserver.message.foobar")
        }

        copy_url.setOnClickListener {
            copyToClipboard("http://127.0.0.1:$port/foobar")
        }
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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu == null) {
            return true
        }
        if (isServiceRunning(BackgroundService::class.java)) {
            menu.findItem(R.id.start_service).isVisible = false
            menu.findItem(R.id.stop_service).isVisible = true
        }
        else {
            menu.findItem(R.id.start_service).isVisible = true
            menu.findItem(R.id.stop_service).isVisible = false
        }
        return true
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

        when (item.itemId) {
            R.id.action_settings -> {
                startActivityForResult(Intent(this, SettingsActivity::class.java), 1)
                return true
            }
            R.id.start_service -> {
                startServer()
                return true
            }
            R.id.stop_service -> {
                stopServer()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}
