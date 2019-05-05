package com.educationShokan.extensions

import io.ktor.routing.Route

val Any.success
    get() = mapOf(
    "status" to "success",
    "data" to this
)

val Any.failure
    get() = mapOf(
        "status" to "failure",
        "data" to this
    )