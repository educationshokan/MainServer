package com.educationShokan.extensions

import java.io.File
import java.nio.file.Files

val String.resourceFile: File
    get() = File("src/main/resources/$this")

val String.mimeType: String
    get() {
        var type = "application/octet-stream"
        val path = File(this).toPath()
        type = Files.probeContentType(path) ?: type
        return type
    }
