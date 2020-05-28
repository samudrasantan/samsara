package org.samsara

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.samsara.category.CategoryCollective
import org.samsara.project.ProjectCollective
import org.samsara.settings.Settings

class MainViewModel(application: Application): AndroidViewModel(application) {





    private val database = RoomDataBase.database(application)


    var account : MutableLiveData<CategoryCollective> = MutableLiveData()
    fun myAccount(user: FirebaseUser, project: ProjectCollective): LiveData<CategoryCollective> = Repo.myAccount(user, project)
    val liveAccount: LiveData<CategoryCollective> = account


    var category: MutableLiveData<CategoryCollective> = MutableLiveData()
    var liveCategory :LiveData<CategoryCollective> = category




    val settingsData:LiveData<Settings> = database.settingsDao().getAllSettings().asLiveData()
    fun insertSetting(setting:Settings)
    {
        CoroutineScope(IO).launch {
            database.settingsDao().insert(setting)
        }
    }
    fun updateSetting(setting: Settings) = CoroutineScope(Dispatchers.IO).launch { database.settingsDao().update(setting) }





    var currentProject: MutableLiveData<ProjectCollective> = MutableLiveData()
    val liveProject: LiveData<ProjectCollective> = currentProject
    fun currentProject(projectId: String):LiveData<ProjectCollective> = Repo.currentProject(projectId)






    var allCategory: MutableLiveData<List<CategoryCollective>>  = MutableLiveData()
    var liveAllCategory: LiveData<List<CategoryCollective>> = allCategory
    fun allCategory(project: ProjectCollective): LiveData<List<CategoryCollective>> = Repo.allCategory(project)



    var categoryType:MutableLiveData<Int> = MutableLiveData()
    val liveCategoryType:LiveData<Int> = categoryType


    val destinationType: MutableLiveData<Int> = MutableLiveData()
    val liveDestinationType: LiveData<Int> = destinationType

    val transactionType: MutableLiveData<Int> = MutableLiveData()
    val liveTransactionType: LiveData<Int> = transactionType

    val projectDescription: MutableLiveData<ProjectCollective> = MutableLiveData()
    val liveProjectDescription : LiveData<ProjectCollective> = projectDescription

    val summaryType: MutableLiveData<Int> = MutableLiveData()
    val liveSummaryType: LiveData<Int> = summaryType

    val reportCategory: MutableLiveData<CategoryCollective> = MutableLiveData()
    val liveReportCategory: LiveData<CategoryCollective> = reportCategory

    val projectType : MutableLiveData<Int> = MutableLiveData()
    val liveProjectType : LiveData<Int> = projectType





}