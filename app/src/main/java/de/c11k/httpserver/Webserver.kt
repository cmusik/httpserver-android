package de.c11k.httpserver

import android.content.Context
import android.content.Intent
import android.util.Log
import fi.iki.elonen.NanoHTTPD

class Webserver(private val context: Context?, host: String, port: Int) : NanoHTTPD(host, port) {

    private val tag = Webserver::class.java.name

    override fun serve(session: IHTTPSession?): Response {
        val path = fixPath(session!!.uri.toString())

        val ret : Response

        if (path != "") {
            val intent = Intent() as Intent?

            val action = "de.c11k.httpserver.message.$path"

            Log.d(tag, "Broadcasting action $action")
            intent!!.action = action
            context!!.sendBroadcast(intent)

            ret = newFixedLengthResponse("got request\n")
        }
        else {
            Log.d(tag, "got empty request, no intent sent")
            ret = newFixedLengthResponse("got empty request, no intent sent")
        }

        return ret
    }

    fun fixPath(str : String) : String {
        return str.substring(1).replace("[^a-z0-9]+".toRegex(RegexOption.IGNORE_CASE), "")
    }
}