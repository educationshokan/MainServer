package com.educationShokan.models

import org.bson.codecs.pojo.annotations.BsonId

data class Project(
    @BsonId override var id: String = "",
    val name: String,
    val files: List<String> = listOf()
) : Identifiable

interface Identifiable {
    var id: String
}