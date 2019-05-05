package com.educationShokan.routes

import io.ktor.application.Application
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.routes() = routing {
    route("/project", Route::project)
}