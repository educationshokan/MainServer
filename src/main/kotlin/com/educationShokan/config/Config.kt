package com.educationShokan.config


import com.educationShokan.extensions.applicationResource
import com.educationShokan.models.DatabaseConfig
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files

fun Application.config() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    install(CORS) {
        method(HttpMethod.Options)
        allowSameOrigin = true
        anyHost()
        this.methods.add(HttpMethod.Put)
        this.methods.add(HttpMethod.Delete)
        exposeHeader("Location")
    }
    install(Routing)
}