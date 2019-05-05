package com.educationShokan.models

data class ProjectReq(val name: String, val template: String?)
data class ProjectUpdate(val name: String?, val files: List<String>?)

data class FileUploadReq(val fileName: String, val fileType: String, val description: String?) {
    fun toMediaFile() = MediaFile(
        fileName = this.fileName,
        fileType = this.fileType,
        description = this.description ?: ""
    )
}