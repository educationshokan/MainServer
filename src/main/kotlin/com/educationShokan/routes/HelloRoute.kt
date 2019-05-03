package com.educationShokan.routes

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

fun Route.hello() {
    get {
        call.respond("Hello there")
    }
}