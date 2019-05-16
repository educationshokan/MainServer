package com.educationShokan.routes

import io.ktor.application.Application
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.routes() = routing {
    //trace { println(it.buildText()) }
    route("/project", Route::project)
    route("/media", Route::mediaFile)
    route("/deploy", Route::deploy)
    route("/publish", Route::published)
}