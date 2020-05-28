package org.samsara.project

data class Collaborators
    (
    var userId: String,
    var name: String,
    var creator: Boolean = false
)