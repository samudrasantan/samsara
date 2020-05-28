package org.samsara.project

import com.google.firebase.firestore.Exclude

data class ProjectCollective
    (
    @Exclude var projectId: String = "",
    var projectName:String ="",
    var projectType: Int = 1,
    var members: List<String> = arrayListOf()
)