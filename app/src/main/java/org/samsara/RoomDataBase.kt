package org.samsara

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import org.samsara.settings.SettingsDao
import org.samsara.settings.Settings


@Database(entities = [Settings::class], version = 1, exportSchema = false)
//@TypeConverters(Converters::class)
abstract  class RoomDataBase : RoomDatabase()
{

    //abstract fun projectDao() : ProjectsDao
    //abstract fun categoriesDao() :CategoriesDao
    //abstract fun transactionsDao() : TransactionsDao
    abstract fun settingsDao() : SettingsDao


    companion object{

        private var database : RoomDataBase? = null

        fun database(context: Context) : RoomDataBase
        {
            val base = database
            if (base != null) {
                return base
            }

            else
            {
                synchronized(this){
                    val x = androidx.room.Room.databaseBuilder(
                        context.applicationContext,
                        RoomDataBase::class.java,
                        Storage.database
                    ).build()

                    database = x

                    return x

                }
            }
        }
    }
}