package com.educationShokan.config

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS

fun Application.config() {
    install(CORS) {
        allowSameOrigin = true
    }
}