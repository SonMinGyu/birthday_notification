package org.application.birthday_notification.`object`

import android.util.Log
import org.application.birthday_notification.model.User
import java.text.SimpleDateFormat
import java.util.*

class GetFriendsBirthday {
    companion object {
        var allBirthdayList: MutableList<User> = mutableListOf()
        var todayBirthdayList: MutableList<User> = mutableListOf()
        var clickedBirthdayList: MutableList<User> = mutableListOf()
    }

    fun updateList(newList: MutableList<User>) {
        allBirthdayList = newList.sortedBy {
            it.birthday
        } as MutableList<User>

    }

    fun getTodayBirth() {
        var dateString: String =
            SimpleDateFormat("MMdd", Locale.KOREA).format(Calendar.getInstance().time)
        Log.d("GetFriendsBirthday", dateString)

        for (i in 0 until allBirthdayList.size) {
            if (allBirthdayList.get(i).birthday == dateString) {
                todayBirthdayList.add(allBirthdayList.get(i))
            }
        }
    }

    fun getClickedBirth(clickedDate: String) {
        for (i in 0 until allBirthdayList.size) {
            if (allBirthdayList.get(i).birthday == clickedDate) {
                clickedBirthdayList.add(allBirthdayList.get(i))
            }
        }
    }

    fun cleanClickedBirthList() {
        clickedBirthdayList.clear()
    }
}