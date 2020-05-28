package org.samsara.project

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.samsara.Storage


@Entity(tableName = Storage.project)
data class Projects (
@PrimaryKey(autoGenerate = true) @ColumnInfo(name = Storage.projectId) var projectId:Int = 0,
@ColumnInfo(name = Storage.projectName) var projectName: String = "",
@ColumnInfo(name = Storage.projectType) var projectType: Int = 0
)
{

    override fun toString(): String {
        return projectName
    }
}