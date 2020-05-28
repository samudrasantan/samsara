package org.samsara.settings

import androidx.room.*
import org.samsara.settings.Settings
import kotlinx.coroutines.flow.Flow
import org.samsara.Storage

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(settings: Settings)

    @Query("SELECT * FROM ${Storage.settings} WHERE ${Storage.settingsId} = 1")
    fun getAllSettings(): Flow<Settings>

    @Update
    suspend fun update(vararg settings: Settings): Int
    @Query("UPDATE ${Storage.settings} SET ${Storage.isLocalProject} = :isLocal, ${Storage.collectiveProjectId} = :id WHERE ${Storage.settingsId} = 1")
    suspend fun updateItems(isLocal: Boolean, id:String)





}