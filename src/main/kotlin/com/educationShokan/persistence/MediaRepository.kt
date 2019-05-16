package com.educationShokan.persistence

import com.educationShokan.database.Database
import com.educationShokan.exceptions.NotFoundException
import com.educationShokan.models.MediaFile
import org.bson.conversions.Bson
import org.litote.kmongo.eq
import kotlin.reflect.full.memberProperties

object MediaRepository : CrudRepository<MediaFile>(Database.db.getCollection(), MediaFile::class) {

    suspend fun readAll() = collection.find().toList()

    suspend fun findByName(name: String): MediaFile = collection.findOne(nameFilter(name)) ?:
        throw NotFoundException("Element of type ${clazz.simpleName} with id $name was not found")

    private fun nameFilter(name: String): Bson {
        val prop = clazz.memberProperties.find { it.name == "fileName" } ?: throw Exception("Reflection Prop not found")
        return prop eq name
    }

}