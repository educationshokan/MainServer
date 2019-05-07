package com.educationShokan.routes

import com.educationShokan.exceptions.NotFoundException
import com.educationShokan.exceptions.exceptionally
import com.educationShokan.extensions.failure
import com.educationShokan.extensions.success
import com.educationShokan.models.*
import com.educationShokan.persistence.MediaRepository
import com.educationShokan.persistence.ProjectRepository
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.project() {

    /*
    * Endpoint for reading a single Project
    */
    get("/{id}") {
        exceptionally {
            val id = call.parameters["id"] ?: ""
            val project = ProjectRepository.read(id)
            call.respond(project.success)
        }
    }

    /*
    * Endpoint for reading all Projects
    */
    get {
        val idList = ProjectRepository.readAllId()
        call.respond(idList.success)
    }

    /*
    * Endpoint for creating a new Project
    */
    post {
        val request = call.receive<ProjectReq>()
        val project = Project(name = request.name)
        val id = ProjectRepository.create(project)
        call.response.header("Location", "/project/$id")
        call.respond(HttpStatusCode.Created)
    }

    /*
    * Endpoint for modifying a given Project
    */
    put("/{id}") {
        val request = call.receive<ProjectUpdate>()
        val id = call.parameters["id"] ?: ""
        var project = try {
            ProjectRepository.read(id)
        } catch (e: NotFoundException) {
            if (request.name == null) {
                call.response.status(HttpStatusCode.NotFound)
                call.respond("Element not found, name property not present in request".failure)
            } else {
                ProjectRepository.create(Project(name = request.name))
                call.response.header("Location", "/project/$id")
                call.respond(HttpStatusCode.Created)
            }
            return@put
        }
        project = project.copy(
            name = request.name ?: project.name,
            files = request.files ?: project.files
        )
        ProjectRepository.update(project)
    }

    /*
    * Endpoint for adding existing files to a given Project
    */
    put("/{id}/addFile") {
        exceptionally {
            val id = call.parameters["id"] ?: ""
            val request = call.receive<FileAddReq>()
            val project = ProjectRepository.read(id)
            val addedList = mutableListOf<String>()
            val failedList = mutableListOf<String>()
            suspend fun sortAccepted(id: String) = (if (MediaRepository.exists(id)) addedList else failedList).add(id)
            request.id?.let { sortAccepted(it) }
            request.idList?.run { forEach { sortAccepted(it) } }
            ProjectRepository.addFiles(project.id, addedList)
            when {
                failedList.isEmpty() -> call.respond(HttpStatusCode.NoContent)
                addedList.isEmpty() -> call.respond(HttpStatusCode.NotFound, "")
                else -> {
                    val payload = addedList.map { mapOf("status" to "success", "id" to it, "code" to 204) } union
                        failedList.map { mapOf("status" to "failure", "id" to it, "code" to 404) }
                        .toList()
                    call.respond(HttpStatusCode.MultiStatus, payload)
                }
            }
        }
    }

    /*
    * Endpoint for deleting a given Project
    */
    delete("/{id}") {
        val id = call.parameters["id"] ?: ""
        ProjectRepository.delete(id)
        call.respond(HttpStatusCode.NoContent)
    }

}