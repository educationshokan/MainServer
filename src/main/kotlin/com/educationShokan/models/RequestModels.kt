package com.educationShokan.models

import com.educationShokan.extensions.mimeType

data class ProjectReq(val name: String, val description: String?, val template: String?)
data class ProjectUpdate(val name: String?, val description: String?, val files: List<String>?)

data class FileUploadReq(val fileName: String, val description: String?) {
    fun toMediaFile() = MediaFile(
        fileName = this.fileName,
        mimeType = this.fileName.mimeType,
        description = this.description ?: ""
    )
}

data class FileAddReq(
    val id: String?,
    val idList: List<String>?
)

data class DeployReq(
    val id: String,
    val excludedFiles: List<String>?
)