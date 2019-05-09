package com.educationShokan.exceptions

import com.educationShokan.extensions.failure
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.pipeline.PipelineInterceptor

suspend fun PipelineContext<Unit, ApplicationCall>.exceptionally(
    fn: PipelineInterceptor<Unit, ApplicationCall>
) {
    try {
        fn(this, Unit)
    } catch (e: NotFoundException) {
        val error = e.message?.failure ?: e.toString().failure
        this.call.respond(HttpStatusCode.NotFound, error)
    } catch (e: Exception) {
        e.printStackTrace()
        this.call.respond(HttpStatusCode.InternalServerError)
    }
}

class NotFoundException(message: String) :  Exception(message)