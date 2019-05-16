package com.educationShokan.routes

import com.educationShokan.exceptions.NotFoundException
import com.educationShokan.exceptions.exceptionally
import com.educationShokan.extensions.failure
import com.educationShokan.extensions.resourceFile
import com.educationShokan.extensions.success
import com.educationShokan.models.FileUploadReq
import com.educationShokan.persistence.MediaRepository
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.request.receiveStream
import io.ktor.request.receiveText
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.response.respondBytes
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import java.util.*

fun Route.mediaFile() {

    post {
        val request = call.receive<FileUploadReq>()
        val id = MediaRepository.create(request.toMediaFile())
        call.response.header("Location", "/media/stream/$id")
        call.respond(mapOf(
            "location" to "/media/stream/$id"
        ).success)
    }

    post("/stream/{id}") {
        exceptionally {
            val id = call.parameters["id"] ?: ""
            val encoded = call.request.queryParameters["encoded"] ?: "false"
            if (encoded == "true") {
                val text = call.receiveText()
                    .replace("data:image/png;base64,", "")
                val decoded = Base64.getDecoder().decode(text)
                val mediaFile = MediaRepository.read(id)
                val storageFile = "storage/${mediaFile.id}".resourceFile
                storageFile.writeBytes(decoded)
                call.respond(HttpStatusCode.Created)
            } else {
                val stream = call.receiveStream()
                val mediaFile = MediaRepository.read(id)
                val storageFile = "storage/${mediaFile.id}".resourceFile
                val bytes = stream.readBytes()
                storageFile.writeBytes(bytes)
                call.respond(HttpStatusCode.Created)
            }

        }
    }

    get {
        val fileList = MediaRepository.readAllId()
        call.respond(fileList.success)
    }

    get("/{id}") {
        exceptionally {
            val id = call.parameters["id"] ?: ""
            val mediaFile = MediaRepository.read(id)
            call.respond(mediaFile.success)
        }
    }

    get("/stream/{id}") {
        exceptionally {
            val id = call.parameters["id"] ?: ""
            val download = call.request.queryParameters["download"] ?: "false"
            val isDownload = download == "true"
            val mediaFile = MediaRepository.read(id)
            val file = "storage/${mediaFile.id}".resourceFile
            if (!file.exists()) throw NotFoundException("File with id $id not found in storage")
            val bytes = file.readBytes()
            val dispositionType = if (!isDownload) "inline" else "attachment"
            call.response.header("Content-Disposition", "$dispositionType; filename=${mediaFile.fileName}")
            call.respondBytes(bytes, ContentType.parse(mediaFile.mimeType))
        }
    }

    delete("/{id}") {
        val id = call.parameters["id"] ?: ""
        if (MediaRepository.delete(id)) {
            "storage/$id".resourceFile.delete()
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.NotFound, "File does not exist".failure)
        }
    }
}