package com.educationShokan.routes

import com.educationShokan.exceptions.NotFoundException
import com.educationShokan.exceptions.exceptionally
import com.educationShokan.extensions.resourceFile
import com.educationShokan.extensions.success
import com.educationShokan.models.DeployReq
import com.educationShokan.models.Deployment
import com.educationShokan.models.MediaFile
import com.educationShokan.models.Project
import com.educationShokan.persistence.DeployRepository
import com.educationShokan.persistence.MediaRepository
import com.educationShokan.persistence.ProjectRepository
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.response.respondBytes
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files

suspend fun deploy(project: Project, deployId: String, excluded: List<String>?): Pair<Boolean, List<String>> {
    val depFiles = project.files subtract (excluded ?: listOf())
    val folder = "deploy/$deployId".resourceFile
    if (folder.exists()) return Pair(false, listOf())
    Files.createDirectories(folder.toPath())
    depFiles.forEach {
        var bytes = "storage/$it".resourceFile.readBytes()
        val fileData = MediaRepository.read(it)
        bytes = if (fileData.mimeType == "text/html") replaceHtml(bytes, deployId) else bytes
        val destination = File("$folder/$it")
        destination.writeBytes(bytes)
    }
    return Pair(true, depFiles.toList())
}

fun replaceHtml(bytes: ByteArray, deployId: String): ByteArray {
    val charset = Charset.forName("UTF-8")
    val text = bytes.toString(charset)
    return text.replace("<id>", deployId).toByteArray(charset)
}

fun Route.deploy() {

    post {
        exceptionally {
            val request = call.receive<DeployReq>()
            val project = ProjectRepository.read(request.id)
            val deployment = Deployment(operationStatus = "pending", projectId = project.id)
            val id = DeployRepository.create(deployment)
            val asyncOperation = async(Dispatchers.IO) { deploy(project, id,  request.excludedFiles) }
            launch {
                val (success, files) = asyncOperation.await()
                DeployRepository.update(deployment.copy(
                    id = id,
                    deployedFiles = files,
                    operationStatus = if (success) "deployed" else "failure"
                ))
            }
            call.respond(HttpStatusCode.Accepted, deployment.success)
        }
    }

    get("/{id}") {
        exceptionally {
            val id = call.parameters["id"] ?: ""
            val deployment = DeployRepository.read(id)
            call.respond(deployment.success)
        }
    }

}

fun Route.published() {

    get("/{id}/{file}") {
        exceptionally {
            val deployId = call.parameters["id"] ?: ""
            val file = call.parameters["file"] ?: ""
            val fileData = MediaRepository.findByName(file)
            val deployment = DeployRepository.read(deployId)
            if (fileData.id !in deployment.deployedFiles)
                throw NotFoundException("The file $file does not exist in this URL")
            val bytes = "deploy/${deployment.id}/${fileData.id}".resourceFile.readBytes()
            call.response.header("Content-Disposition", "inline; filename=${fileData.fileName}")
            call.respondBytes(bytes, ContentType.parse(fileData.mimeType))
        }
    }

}