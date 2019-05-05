package com.educationShokan.routes

import com.educationShokan.extensions.resourceFile
import com.educationShokan.extensions.success
import com.educationShokan.models.FileUploadReq
import com.educationShokan.persistence.MediaRepository
import io.ktor.application.call
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.request.receiveStream
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.response.respondBytes
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post

fun Route.mediaFile() {

    post {
        val request = call.receive<FileUploadReq>()
        val id = MediaRepository.create(request.toMediaFile())
        call.response.header("Location", "/media/stream/$id")
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
        val storageFile = "storage/${mediaFile.id}".resourceFile
        storageFile.writeBytes(bytes)
        call.respond(HttpStatusCode.Created)
    }

    get("/{id}") {
        try {
            val id = call.parameters["id"] ?: ""
            val mediaFile = MediaRepository.read(id)
            val bytes = "storage/${mediaFile.id}".resourceFile.readBytes()
            call.response.header("Content-Disposition", "inline; filename=${mediaFile.fileName}")
            call.respondBytes(bytes, ContentType.parse(mediaFile.mimeType))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}