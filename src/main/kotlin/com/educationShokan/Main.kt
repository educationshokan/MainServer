package com.educationShokan

import com.educationShokan.config.config
import com.educationShokan.extensions.resourceFile
import com.educationShokan.routes.routes
import io.ktor.application.Application

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.nio.file.Files

fun main() {
    val exists = Files.exists("/".resourceFile.toPath())
    if (!exists) {
        listOf("/storage", "/deploy").forEach {
            Files.createDirectories(it.resourceFile.toPath())
        }
    }
    val server = embeddedServer(Netty, 8080, module = Application::main)
    server.start(wait = true)
}

fun Application.main() {
    config()
    routes()
}