package de.c11k.httpserver

import org.junit.Assert
import org.junit.Test

class WebserverUnitTest {
    @Test
    fun testValidPath() {
        val obj = Webserver(null, "localhost", 8080)

        Assert.assertEquals("test", obj.fixPath("/test"))
        Assert.assertEquals("atest", obj.fixPath("/atest"))
        Assert.assertEquals("TEST", obj.fixPath("/TEST"))
        Assert.assertEquals("test123", obj.fixPath("/test123"))
        Assert.assertEquals("test123", obj.fixPath("/test123!"))
        Assert.assertEquals("", obj.fixPath("/!"))
    }
}