package com.educationShokan.routes

import com.educationShokan.exceptions.NotFoundException
import com.educationShokan.extensions.failure
import com.educationShokan.extensions.success
import com.educationShokan.models.Project
import com.educationShokan.models.ProjectReq
import com.educationShokan.models.ProjectUpdate
import com.educationShokan.persistence.ProjectRepository
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.project() {

    get("/{id}") {
        val id = call.parameters["id"] ?: ""
        try {
            val project = ProjectRepository.read(id)
            call.respond(project.success)
        } catch (e: NotFoundException) {
            val error = e.message?.failure ?: e.toString().failure
            call.response.status(HttpStatusCode.NotFound)
            call.respond(error)
        }
    }

    get {
        val idList = ProjectRepository.readAllId()
        call.respond(idList.success)
    }

    post {
        val request = call.receive<ProjectReq>()
        val project = Project(name = request.name)
        val id = ProjectRepository.create(project)
        call.response.header("Location", "/project/$id")
        call.respond(HttpStatusCode.Created)
    }

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

    delete("/{id}") {
        val id = call.parameters["id"] ?: ""
        ProjectRepository.delete(id)
        call.respond(HttpStatusCode.NoContent)
    }

}