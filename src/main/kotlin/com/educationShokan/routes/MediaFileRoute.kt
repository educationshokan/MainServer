package com.educationShokan.routes

import com.educationShokan.extensions.failure
import com.educationShokan.extensions.getResourceFile
import com.educationShokan.extensions.success
import com.educationShokan.models.FileUploadReq
import com.educationShokan.persistence.MediaRepository
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.request.receiveStream
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post

fun Route.mediaFile() {

    post {
        val request = call.receive<FileUploadReq>()
        val id = MediaRepository.create(request.toMediaFile())
        call.response.header("Location", "/project/stream/$id")
        //call.response.header("Content-Length", 0)
        call.respond(mapOf(
            "location" to "/media/stream/$id"
        ).success)
    }

    post("/stream/{id}") {
        val id = call.parameters["id"] ?: ""
        val stream = call.receiveStream()
        val bytes = stream.readBytes()
        val mediaFile = MediaRepository.read(id)
        val storageFile = "storage/${mediaFile.fileName}".getResourceFile()
        storageFile.writeBytes(bytes)
        call.respond(HttpStatusCode.Created)
    }

}