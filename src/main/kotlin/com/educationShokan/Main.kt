package com.educationShokan

import com.educationShokan.config.config
import com.educationShokan.routes.routes
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val server = embeddedServer(Netty, 8080, module = Application::main)
    server.start(wait = true)
}

fun Application.main() {
    config()
    routes()
}