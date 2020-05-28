package org.samsara

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import org.samsara.project.ProjectCollective

object Storage {

    val log = "LOG"

    const val database = "samsara"

    const val userId = "userId"

    const val project = "projects"
    const val projectId = "projectId"
    const val localProjectId = "localProjectId"
    const val collectiveProjectId = "collectiveProjectId"
    const val isLocalProject = "isLocalProject"
    const val projectName = "projectName"
    const val projectType = "projectType"

    const val category = "categories"
    const val categoryId = "categoryId"
    const val categoryName = "categoryName"


    const val transaction = "transactions"
    const val transactionId = "transactionId"
    const val amount = "amount"
    const val date = "date"
    const val transactionAmount = "transactionAmount"
    const val categoryBalance = "categoryBalance"

    const val settings = "settings"
    const val settingsId = "settingsId"
    const val theme = "theme"

    const val collaborators = "collaborators"
    const val collaboratorId = "collaboratorId"

    var auth: FirebaseAuth = FirebaseAuth.getInstance()



    var currentProject: MutableLiveData<ProjectCollective> = MutableLiveData()
    var liveProject: LiveData<ProjectCollective> = currentProject
}