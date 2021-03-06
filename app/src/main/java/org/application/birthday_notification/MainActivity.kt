package org.application.birthday_notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.*
import org.application.birthday_notification.Fragment.AlarmSetupFragment
import org.application.birthday_notification.Fragment.BirthdayListFragment
import org.application.birthday_notification.Fragment.CalenderFragment
import org.application.birthday_notification.`object`.GetFriendsBirthday
import org.application.birthday_notification.`object`.GetFriendsBirthday.Companion.allBirthdayList
import org.application.birthday_notification.`object`.GetFriendsBirthday.Companion.todayBirthdayList
import org.application.birthday_notification.alarm.AlarmReceiver
import org.application.birthday_notification.model.User
import org.application.birthday_notification.room.UserInfo
import org.application.birthday_notification.room.UserInfoDB
import java.util.*


private const val NUM_PAGE = 3

class MainActivity : AppCompatActivity() {

    companion object {
        const val ALARM_REQUEST_CODE = 2000
    }

    lateinit var viewPager: ViewPager2
    lateinit var updateFriendsBirthdayImageView: ImageView

    // DataBase
    private var userInfoDatabase: UserInfoDB? = null
    private var userInfoList: MutableList<UserInfo> =
        mutableListOf<UserInfo>() // room??? ?????? ????????? db, ????????? ?????? ?????? ?????? X
    private var userList: MutableList<User> =
        mutableListOf<User>() // ????????? ????????? ????????? ?????? ???????????? db, ????????? ?????? ?????? ?????? O

    private val tabTextList = arrayListOf<Int>(
        R.drawable.ic_launcher_background,
        R.drawable.calendar_icon,
        R.drawable.ic_launcher_background
    )

    //    1763014771 (gamil)
    //    1760881071 (hanmail)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context = this@MainActivity

        // db ??????
//        UserInfoDB.destroyInstance()

        updateFriendsBirthdayImageView = findViewById(R.id.main_update_friends_birthday_imageview)
        updateFriendsBirthdayImageView.setOnClickListener {
            val intent = Intent(this@MainActivity, UpdateFriendsBirthday::class.java)
            startActivity(intent)
        }

        viewPager = findViewById(R.id.main_birthdayList_view_pager)
        val pagerAdapter = PagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.run { isUserInputEnabled = false }

        var tab_layout = findViewById<TabLayout>(R.id.main_birthdayList_tab_layout)
        TabLayoutMediator(tab_layout, viewPager) { tab, position ->
            tab.setIcon(tabTextList[position])
        }.attach()

//        val token_info_button: Button = findViewById(R.id.token_info_button)
//        val user_info_button: Button = findViewById(R.id.user_info_button)
//        val logout_button: Button = findViewById(R.id.logout_button)
        val disconnect_button: Button = findViewById(R.id.main_todayBirthday_disconnect_button)
        val getFriends_button: Button = findViewById(R.id.main_todayBirthday_getFriends_button)

        // ?????? ????????? ????????? ?????? ?????? ID??? ??????
        val scopes = mutableListOf("friends", "age_range")
        var friendsAgree: Boolean? = false

        UserApiClient.instance.scopes(scopes) { scopeInfo, error ->
            if (error != null) {
                Log.d("mainActivity", "?????? ?????? ?????? ??????" + error)
            } else if (scopeInfo != null) {
                Log.d("mainActivity", "?????? ?????? ?????? ??????\n ?????? ????????? ?????? ?????? ?????? $scopeInfo")
                friendsAgree = scopeInfo?.scopes?.get(0)?.agreed
                Log.d("mainActivity", friendsAgree.toString())
            }
        }

        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.d("mainActivity", "????????? ?????? ?????? ??????" + error)
            } else if (user != null) {
                var scopes = mutableListOf<String>()

                if (friendsAgree != true) {
                    scopes.add("friends")
                }
//                    if (user.kakaoAccount?.emailNeedsAgreement == true) {
//                        scopes.add("account_email")
//                    }
//                    if (user.kakaoAccount?.birthdayNeedsAgreement == true) { scopes.add("birthday") }
//                    if (user.kakaoAccount?.birthyearNeedsAgreement == true) { scopes.add("birthyear") }
//                    if (user.kakaoAccount?.genderNeedsAgreement == true) { scopes.add("gender") }
//                    if (user.kakaoAccount?.phoneNumberNeedsAgreement == true) { scopes.add("phone_number") }
//                    if (user.kakaoAccount?.profileNeedsAgreement == true) { scopes.add("profile") }
                if (user.kakaoAccount?.ageRangeNeedsAgreement == true) {
                    scopes.add("age_range")
                }
//                    if (user.kakaoAccount?.ciNeedsAgreement == true) { scopes.add("account_ci") }

                Log.d("mainActivity", "?????? ??????" + scopes.count())
                if (scopes.count() > 0) {
                    Log.d("mainActivity", "??????????????? ?????? ????????? ????????? ?????????.")

                    UserApiClient.instance.loginWithNewScopes(context, scopes) { token, error ->
                        if (error != null) {
                            Log.d("mainActivity", "????????? ?????? ?????? ??????" + error)
                        } else {
                            Log.d("mainActivity", "allowed scopes: ${token!!.scopes}")

                            // ????????? ?????? ?????????
                            UserApiClient.instance.me { user, error ->
                                if (error != null) {
                                    Log.d("mainActivity", "????????? ?????? ?????? ??????" + error)
                                } else if (user != null) {
                                    Log.d("mainActivity", "????????? ?????? ?????? ??????")
                                }
                            }
                        }
                    }
                }
            }
        }

        /////////////////////////////
        var birthdayArray: MutableList<User> = mutableListOf()

        birthdayArray.add(User(0, "son", "1558"))
        birthdayArray.add(User(1, "son1", "1600"))
        birthdayArray.add(User(2, "son2", "1604"))
        birthdayArray.add(User(3, "son3", "1608"))
        birthdayArray.add(User(4, "son4", "1610"))
        birthdayArray.add(User(5, "son5", "1615"))
        birthdayArray.add(User(6, "son6", "1620"))
        birthdayArray.add(User(7, "son7", "1804"))
        birthdayArray.add(User(8, "son8", "1806"))
        birthdayArray.add(User(9, "son9", "1814"))
        birthdayArray.add(User(10, "son10", "1830"))
        birthdayArray.add(User(11, "son11", "1840"))
        birthdayArray.add(User(12, "son12", "1850"))
        birthdayArray.add(User(13, "son13", "1900"))

//        for (i in 0..9) {
//            birthdayArray.add(i, User("" + (i + 1), (i + 1) * 111111))
//        }

        val birthdayListObject: GetFriendsBirthday = GetFriendsBirthday()
        birthdayListObject.updateList(birthdayArray)
        birthdayListObject.getTodayBirth()

        Log.d("mainActivity", "birthdaylist: " + allBirthdayList)
        Log.d("mainActivity", "birthdaylist: " + todayBirthdayList)
        ///////////////////////////////


//        getDB()
        setBirthdayAlarm()


        val todayBirthDayadapter =
            MainTodayBirthdayAdapter(todayBirthdayList, LayoutInflater.from(this@MainActivity))
        findViewById<RecyclerView>(R.id.main_todayBirthday_recyclerview).adapter =
            todayBirthDayadapter
        findViewById<RecyclerView>(R.id.main_todayBirthday_recyclerview).layoutManager =
            LinearLayoutManager(this@MainActivity)

        //// ????????? allBirthDay recyclerview ??????


//        if (friendsAgree == true) {
//            Log.d("mainActivity", "???????????? ??????")
//        } else {
//
//        }

//        val friendsAgree =

//        token_info_button.setOnClickListener {
//            // ?????? ?????? ??????
//            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
//                if (error != null) {
//                    Log.d("login", "?????? ?????? ?????? ??????" + error)
//                } else if (tokenInfo != null) {
//                    Log.d(
//                        "login", "?????? ?????? ?????? ??????" +
//                                "\n????????????: ${tokenInfo.id}" +
//                                "\n????????????: ${tokenInfo.expiresIn} ???"
//                    )
//                }
//            }
//        }
//
//        user_info_button.setOnClickListener {
//            // ????????? ?????? ?????? (??????)
//            UserApiClient.instance.me { user, error ->
//                if (error != null) {
//                    Log.d("login", "????????? ?????? ?????? ??????" + error)
//                } else if (user != null) {
//                    Log.d(
//                        "login", "????????? ?????? ?????? ??????" +
//                                "\n????????????: ${user.id}" +
//                                "\n?????????: ${user.kakaoAccount?.email}" +
//                                "\n?????????: ${user.kakaoAccount?.profile?.nickname}" +
//                                "\n???????????????: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
//                    )
//                }
//            }
//        }
//
//        logout_button.setOnClickListener {
//            UserApiClient.instance.logout { error ->
//                if (error != null) {
//                    Log.d("login", "????????????  ??????. SDK?????? ?????? ?????????" + error)
//                } else {
//                    Log.d("login", "???????????? ??????. SDK?????? ?????? ?????????")
//                }
//            }
//        }
//
        disconnect_button.setOnClickListener {
            // ?????? ??????
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Log.d("mainActivity", "?????? ?????? ??????" + error)
                } else {
                    Log.d("mainActivity", "?????? ?????? ??????. SDK?????? ?????? ?????? ???")
                }
            }
        }

        getFriends_button.setOnClickListener {

//            TalkApiClient.instance.profile { profile, error ->
//                Log.d("mainActivity", profile?.nickname.toString())
//            }

            TalkApiClient.instance.friends { friends, error ->
                if (error != null) {
                    Log.d("mainActivity", "???????????? ?????? ?????? ???????????? ??????" + error)
                } else if (friends != null) {
                    Log.d(
                        "mainActivity",
                        "???????????? ?????? ?????? ???????????? ?????? \n${friends.elements?.joinToString("\n")}"
                    )
                    Log.d("mainActivity", "" + friends.elements?.size)

                    // ????????? UUID ??? ????????? ????????? ??????
                }
            }

            // ????????? ????????? ?????? ????????? ?????????????????? recyclerview??? ???????????? ??????????????? ???????????????, ???????????????
        }
    }

    private fun getDB() {

        userInfoDatabase = UserInfoDB.getInstance(this)

        // db??? ?????? ???????????? ??????, ????????? companion object??? db ???????????? ?????? ??????

        var job = CoroutineScope(Dispatchers.IO).launch {
            for (i in 0 until allBirthdayList.size) {
                val newUserInfo = UserInfo()
                newUserInfo.id = allBirthdayList.get(i).id.toLong()
                newUserInfo.user_name = allBirthdayList.get(i).name.toString()
                newUserInfo.user_birthday = allBirthdayList.get(i).birthday.toString()
                newUserInfo.alarm_set = allBirthdayList.get(i).alarmSet
                userInfoDatabase?.userInfoDao()?.insert(newUserInfo)
            }

        }

        runBlocking {
            job.join()
        }
        Log.d("MainActivity", "getDB??????")
    }

//    private suspend fun updateDB() {
//        CoroutineScope.async(Dispatchers.IO) {
//            for (i in 0 until allBirthdayList.size) {
//                val newUserInfo = UserInfo()
//                newUserInfo.id = allBirthdayList.get(i).id.toLong()
//                newUserInfo.user_name = allBirthdayList.get(i).name.toString()
//                newUserInfo.user_birthday = allBirthdayList.get(i).birthday.toString()
//                newUserInfo.alarm_set = allBirthdayList.get(i).alarmSet
//                userInfoDatabase?.userInfoDao()?.insert(newUserInfo)
//            }
//        }.await()
//    }

    private fun setBirthdayAlarm(hour: Int = 0, minute: Int = 0, request_code: Int = 0) {
        Log.d("MainActivity", "setBirthdayAlarm ??????")
        userInfoDatabase = UserInfoDB.getInstance(this)

        CoroutineScope(Dispatchers.IO).launch {
            userInfoList = userInfoDatabase?.userInfoDao()?.getAll()!!

            for (i in 0 until userInfoList.size) {
                Log.d("MainActivity", "user_birthday" + userInfoList.get(i).user_birthday)
            }

            for (i in 0 until userInfoList.size) {
                if (userInfoList.get(i).alarm_set) {
                    var birthday_charArray = userInfoList.get(i).user_birthday!!.toCharArray()
                    var hour: String =
                        birthday_charArray?.get(0).toString() + birthday_charArray?.get(1)
                            .toString()
                    var minute: String =
                        birthday_charArray?.get(2).toString() + birthday_charArray?.get(3)
                            .toString()

                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.MONTH, 6)
                        set(Calendar.DATE, 23)
                        set(Calendar.HOUR_OF_DAY, hour.toInt())
                        set(Calendar.MINUTE, minute.toInt())
                    }

                    var id_number: Int = userInfoList.get(i).id!!.toInt()
                    var birthday_user_name: String = userInfoList.get(i).user_name!!
                    var birthday: String = userInfoList.get(i).user_birthday!!
                    Log.d("MainActivity", "id_number is: " + id_number)

                    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent = Intent(this@MainActivity, AlarmReceiver::class.java)
                    intent.putExtra("id_number", id_number)
                    intent.putExtra("birthday_user_name", birthday_user_name)
                    intent.putExtra("birthday", birthday)

                    val pendingIntent = PendingIntent.getBroadcast(this@MainActivity,
                        id_number,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT)

                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )
                }
            }

//            birthdayDateArray.distinct()
//            Log.d("mainActivity", "birthdayDateArray size: " + birthdayDateArray.size)
//            Log.d("mainActivity", "birthdayDateArray: " + birthdayDateArray)
        }


        ////////////////////////////////////////////////
//        val calendar = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, 14)
//            set(Calendar.MINUTE, 48)
//        }
//
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(this@MainActivity, AlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(this@MainActivity,
//            1,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT)
//
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_DAY,
//            pendingIntent
//        )
        ////////////////////////////////////////////////

        Log.d("alarmService", "??????23323")
        val serviceIntent = Intent(this@MainActivity, MyService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        // dont kill app ??? ?????? foreground??? ????????? ???????????????
        val receiver = ComponentName(this@MainActivity, MyService::class.java)
        applicationContext.packageManager.setComponentEnabledSetting(receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP)

    }

    private fun removeBirthdayAlarm() {
        val pendingIntent = PendingIntent.getBroadcast(this@MainActivity,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE)
        pendingIntent?.cancel()
    }
}

class MainTodayBirthdayAdapter(
    val birthdayList: MutableList<User>,
    val inflater: LayoutInflater,
) : RecyclerView.Adapter<MainTodayBirthdayAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val friendsName: TextView
        val friendsBirthday: TextView

        init {
            friendsName = itemView.findViewById(R.id.listitem_today_birthday_list_friendsName)
            friendsBirthday =
                itemView.findViewById(R.id.listitem_today_birthday_list_friendsBirthday)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.listitem_today_birthday_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.friendsName.setText(birthdayList.get(position).name)
        holder.friendsBirthday.setText(birthdayList.get(position).birthday)

    }

    override fun getItemCount(): Int {
        return birthdayList.size
    }
}

class PagerAdapter(
    fragment: FragmentActivity,
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return NUM_PAGE
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                return BirthdayListFragment()
            }
            1 -> {
                return CalenderFragment()
            }
            2 -> {
                return AlarmSetupFragment()
            }
            else -> {
                return BirthdayListFragment()
            }

        }
    }
}

    