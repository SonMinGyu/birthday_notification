package org.application.birthday_notification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.application.birthday_notification.model.User
import org.application.birthday_notification.room.UserInfo
import org.application.birthday_notification.room.UserInfoDB

class UpdateFriendsBirthday : AppCompatActivity() {
    private var userInfoDatabase1: UserInfoDB? = null
    private var userInfoDatabase2: UserInfoDB? = null
    private var userInfoList: MutableList<UserInfo> = mutableListOf<UserInfo>()

    lateinit var nameText: EditText
    lateinit var birthdayText: EditText
    lateinit var updateButton: Button
    lateinit var getButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_friends_birthday)

        userInfoDatabase1 = UserInfoDB.getInstance(this)
        userInfoDatabase2 = UserInfoDB.getInstance(this)

        nameText = findViewById(R.id.update_friends_birthday_name_text)
        birthdayText = findViewById(R.id.update_friends_birthday_name_text)
        updateButton = findViewById(R.id.update_friends_birthday_button)
        getButton = findViewById(R.id.get_friends_birthday_button)

        updateButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val newUserInfo = UserInfo()
                newUserInfo.user_name = nameText.text.toString()
                newUserInfo.user_birthday = birthdayText.text.toString()
                userInfoDatabase1?.userInfoDao()?.insert(newUserInfo)
            }
        }


        getButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                userInfoList = userInfoDatabase2?.userInfoDao()?.getAll()!!
                Log.d("UpdateFriendsBirthday", "" + userInfoList.get(userInfoList.size - 1).user_name)
            }
        }
    }
}