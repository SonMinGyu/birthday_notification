package org.application.birthday_notification.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface UserInfoDao {
    @Query("SELECT * FROM userInfo")
    fun getAll(): MutableList<UserInfo>

    /* import android.arch.persistence.room.OnConflictStrategy.REPLACE */
    @Insert(onConflict = REPLACE)
    fun insert(userInfo: UserInfo)

    @Query("DELETE from userInfo")
    fun deleteAll()
}