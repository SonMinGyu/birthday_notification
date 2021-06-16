package org.application.birthday_notification.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserInfo::class], version = 1)
abstract class UserInfoDB : RoomDatabase() {
    abstract fun userInfoDao(): UserInfoDao

    companion object {
        private var INSTANCE: UserInfoDB? = null

        fun getInstance(context: Context): UserInfoDB? {
            if (INSTANCE == null) {
                synchronized(UserInfoDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        UserInfoDB::class.java, "userInfo.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}