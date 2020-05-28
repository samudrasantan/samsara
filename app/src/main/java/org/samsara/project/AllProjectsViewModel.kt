package org.samsara.project

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.samsara.Repo
import org.samsara.RoomDataBase
import org.samsara.settings.Settings

class AllProjectsViewModel(application: Application) : AndroidViewModel(application) {

    private val dataBase = RoomDataBase.database(application)

    fun projectList(user:FirebaseUser):LiveData<List<ProjectCollective>> = Repo.projectList(user)
    fun updateSetting(setting: Settings) = CoroutineScope(Dispatchers.IO).launch { dataBase.settingsDao().update(setting) }


}
