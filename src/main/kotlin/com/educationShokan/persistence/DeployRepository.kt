package com.educationShokan.persistence

import com.educationShokan.database.Database
import com.educationShokan.models.Deployment

object DeployRepository : CrudRepository<Deployment>(Database.db.getCollection(), Deployment::class)