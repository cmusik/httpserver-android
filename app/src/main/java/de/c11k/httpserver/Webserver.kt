package de.c11k.httpserver

import android.content.Context
import android.content.Intent
import android.util.Log
import fi.iki.elonen.NanoHTTPD

class Webserver(private val context: Context, host: String, port: Int) : NanoHTTPD("127.0.0.1", port) {

    private val TAG = Webserver::class.java.name

    override fun serve(session: IHTTPSession?): Response {
        val path = session!!.uri.toString().substring(1).replace("[^a-z]", "", true)

        val intent = Intent() as Intent?

        val action = "de.c11k.httpserver.message.$path"

        Log.d(TAG, "Broadcasting action $action")
        intent!!.action = action
        context.sendBroadcast(intent)

        return newFixedLengthResponse("got request\n")
    }
}