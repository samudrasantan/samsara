package org.samsara.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.samsara.Storage

@Entity(tableName = Storage.settings)
data class Settings (
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = Storage.settingsId) var settingsId: Int = 1,
    @ColumnInfo(name = Storage.localProjectId) var localProjectId: Int = 0,
    @ColumnInfo(name = Storage.collectiveProjectId) var collectiveProjectId: String = "",
    @ColumnInfo(name = Storage.isLocalProject) var isLocalProject: Boolean = false
)