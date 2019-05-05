package com.educationShokan.database

import com.educationShokan.models.Project
import com.mongodb.ConnectionString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.litote.kmongo.reactivestreams.*
import org.litote.kmongo.coroutine.*
import java.lang.NullPointerException

object Database {

    val db: CoroutineDatabase

    init {
        val client = KMongo.createClient(
            "mongodb+srv://master:1AmTheSenate@cluster0-crrrc.mongodb.net/test?retryWrites=true"
        ).coroutine
        db = client.getDatabase("EducationShokan")
        GlobalScope.launch {
            try {
                db.getCollection<Project>().find().toList()
            } catch (e: NullPointerException) {
                println("Database ready")
            }
        }
    }

}