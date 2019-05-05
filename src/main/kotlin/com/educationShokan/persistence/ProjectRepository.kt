package com.educationShokan.persistence

import com.educationShokan.database.Database
import com.educationShokan.models.Project

object ProjectRepository : CrudRepository<Project>(Database.db.getCollection(), Project::class)