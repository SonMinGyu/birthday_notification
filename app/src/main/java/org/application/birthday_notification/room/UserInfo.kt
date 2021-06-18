package org.application.birthday_notification.room

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userInfo")
class UserInfo(
    @PrimaryKey var id: Long?,
    @ColumnInfo(name = "name") var user_name: String?,
    @ColumnInfo(name = "birthday") var user_birthday: String?,
    @ColumnInfo(name = "alarmSet") var alarm_set: Boolean,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as Boolean,
    ) {
    }

    constructor() : this(null, "", "", true)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(user_name)
        parcel.writeString(user_birthday)
        parcel.writeValue(alarm_set)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserInfo> {
        override fun createFromParcel(parcel: Parcel): UserInfo {
            return UserInfo(parcel)
        }

        override fun newArray(size: Int): Array<UserInfo?> {
            return arrayOfNulls(size)
        }
    }
}