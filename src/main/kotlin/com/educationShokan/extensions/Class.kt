package com.educationShokan.extensions

import java.io.File
import java.lang.Exception

fun String.getResourceFile(): File {
    try {
        val file = File("src/main/resources/$this")
        return file
    } catch (e: Exception) {
        e.printStackTrace()
        return File("")
    }
}
