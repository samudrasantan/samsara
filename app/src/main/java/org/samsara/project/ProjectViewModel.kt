package org.samsara.project

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.samsara.Repo
import org.samsara.RoomDataBase
import org.samsara.category.CategoryCollective
import org.samsara.settings.Settings

class ProjectViewModel(application: Application) : AndroidViewModel(application) {

    private val dataBase = RoomDataBase.database(application)

    fun createProjectInFireStore(project:ProjectCollective, user: FirebaseUser, name: String, listener: ProjectAddListener) = Repo.createProjectInFireStore(project, user, name, dataBase, listener)
    fun collaborate(collaborator: Collaborators, projectId:String, category:CategoryCollective, listener:ProjectAddListener) = Repo.collaborate(collaborator, projectId, category, dataBase, listener)
}