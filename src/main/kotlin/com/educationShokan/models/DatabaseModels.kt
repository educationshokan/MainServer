package com.educationShokan.models

import org.bson.codecs.pojo.annotations.BsonId

data class Project(
    @BsonId override var id: String = "",
    val name: String,
    val files: List<String> = listOf()
) : Identifiable

data class MediaFile(
    @BsonId override var id: String = "",
    val fileName: String,
    val mimeType: String,
    val description: String
) : Identifiable

data class Deployment(
    @BsonId override var id: String = "",
    val projectId: String,
    val operationStatus: String,
    val deployedFiles: List<String> = listOf()
): Identifiable

interface Identifiable {
    var id: String
}