package me.utsman.sample

import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT").toInt(), host = "0.0.0.0") {
        configureRoute()
    }.start(wait = true)
}