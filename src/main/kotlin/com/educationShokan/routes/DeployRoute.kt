package com.educationShokan.routes

import com.educationShokan.exceptions.exceptionally
import com.educationShokan.models.DeployReq
import com.educationShokan.models.Deployment
import com.educationShokan.models.Project
import com.educationShokan.models.ProjectReq
import com.educationShokan.persistence.DeployRepository
import com.educationShokan.persistence.ProjectRepository
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

fun deploy(project: Project, excluded: List<String>?): Boolean {
    TODO("not implemented")
}

fun Route.deploy() {

    post {
        exceptionally {
            val request = call.receive<DeployReq>()
            val project = ProjectRepository.read(request.id)
            val asyncOperation = async(Dispatchers.IO) { deploy(project, request.excludedFiles) }
            val deployment = Deployment(operationStatus = "pending")
            val id = DeployRepository.create(deployment)
            launch {
                val success = asyncOperation.await()
                DeployRepository.update(deployment.copy(
                    id = id,
                    operationStatus = if (success) "deployed" else "failure"
                ))
            }
        }
    }

    get("/{id}") {
        TODO("not implemented")
    }

}

fun Route.published() {

    get("/{id}") {
        TODO("not implemented")
    }

}