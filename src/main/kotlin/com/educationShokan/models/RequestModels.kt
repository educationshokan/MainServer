package com.educationShokan.models

import java.util.*

data class ProjectReq(val name: String, val template: String?)
data class ProjectUpdate(val name: String?, val files: List<String>?)