package io.erva.lund.storage.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NotificationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notificationDao(): NotificationDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room
                        .databaseBuilder(context.applicationContext, AppDatabase::class.java, "app.db")
                        .allowMainThreadQueries()
                        .build()
            }
            return INSTANCE as AppDatabase
        }
    }
}