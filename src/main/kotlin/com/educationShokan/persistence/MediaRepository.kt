package com.educationShokan.persistence

import com.educationShokan.database.Database
import com.educationShokan.models.MediaFile

object MediaRepository : CrudRepository<MediaFile>(Database.db.getCollection(), MediaFile::class) {

    suspend fun readAll() = collection.find().toList()

}