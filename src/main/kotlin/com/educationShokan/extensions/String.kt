package com.educationShokan.extensions

import com.educationShokan.database.Database
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Files

val String.resourceFile: File
    get() = File("${System.getProperty("user.dir")}/MainServerResources/$this")

val String.applicationResource: InputStream
    get() {
        val input = Database.javaClass.classLoader.getResourceAsStream(this)
        println(InputStreamReader(input).readText())
        return input
    }

val String.mimeType: String
    get() {
        var type = "application/octet-stream"
        val path = File(this).toPath()
        type = Files.probeContentType(path) ?: type
        return type
    }
