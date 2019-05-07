package com.educationShokan.persistence

import com.educationShokan.database.Database
import com.educationShokan.models.Project

object ProjectRepository : CrudRepository<Project>(Database.db.getCollection(), Project::class) {

    suspend fun addFiles(projectId: String, fileIdList: List<String>) {
        val project = read(projectId)
        val joinedFiles = (fileIdList union project.files).toList()
        val newProject = project.copy(files = joinedFiles)
        update(newProject)
    }

}