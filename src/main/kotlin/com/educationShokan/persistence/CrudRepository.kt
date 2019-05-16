package com.educationShokan.persistence

import com.educationShokan.exceptions.NotFoundException
import com.educationShokan.models.Identifiable
import org.bson.conversions.Bson
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

abstract class CrudRepository<T: Identifiable>(
    protected val collection: CoroutineCollection<T>,
    protected val clazz: KClass<T>
) {

    suspend fun create(element: T): String {
        element.id = UUID.randomUUID().toString()
        collection.insertOne(element)
        return element.id
    }

    suspend fun read(id: String): T = collection.findOne(propertyFilter(id)) ?:
        collection.findOne(propertyFilter(id, "name")) ?:
        throw NotFoundException("Element of type ${clazz.simpleName} with id $id was not found")

    suspend fun readAllId(): List<String> = try {
        collection.find().toList().map { it.id }
    } catch(e: Exception) {
        listOf()
    }

    suspend fun exists(id: String): Boolean = collection.countDocuments(propertyFilter(id)) > 0

    suspend fun delete(id: String): Boolean {
        val result = collection.deleteOne(propertyFilter(id))
        return result.deletedCount > 0
    }

    suspend fun update(element: T) = collection.updateOneById(element.id, element)

    suspend fun wipe() = collection.deleteMany()

    private fun propertyFilter(id: String, propertyName: String = "id"): Bson {
        val prop = clazz.memberProperties.find { it.name == propertyName } ?: throw Exception("Reflection Prop not found")
        return prop eq id
    }

}