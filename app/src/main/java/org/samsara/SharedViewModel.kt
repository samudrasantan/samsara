package org.samsara

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import org.samsara.settings.Settings

class SharedViewModel(application: Application):AndroidViewModel(application) {
    private val database = RoomDataBase.database(application)
    val settingsData: LiveData<Settings> = database.settingsDao().getAllSettings().asLiveData()
}