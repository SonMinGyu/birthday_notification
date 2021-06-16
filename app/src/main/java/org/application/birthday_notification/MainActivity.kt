package org.application.birthday_notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import org.application.birthday_notification.Fragment.AlarmSetupFragment
import org.application.birthday_notification.Fragment.BirthdayListFragment
import org.application.birthday_notification.Fragment.CalenderFragment
import org.application.birthday_notification.`object`.GetFriendsBirthday
import org.application.birthday_notification.`object`.GetFriendsBirthday.Companion.allBirthdayList
import org.application.birthday_notification.`object`.GetFriendsBirthday.Companion.todayBirthdayList
import org.application.birthday_notification.model.User


private const val NUM_PAGE = 3

class MainActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager2
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

        // 동의 내역을 조회할 동의 항목 ID의 목록
        val scopes = mutableListOf("friends", "age_range")
        var friendsAgree: Boolean? = false

        UserApiClient.instance.scopes(scopes) { scopeInfo, error ->
            if (error != null) {
                Log.d("mainActivity", "동의 정보 확인 실패" + error)
            } else if (scopeInfo != null) {
                Log.d("mainActivity", "동의 정보 확인 성공\n 현재 가지고 있는 동의 항목 $scopeInfo")
                friendsAgree = scopeInfo?.scopes?.get(0)?.agreed
                Log.d("mainActivity", friendsAgree.toString())
            }
        }

        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.d("mainActivity", "사용자 정보 요청 실패" + error)
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

                Log.d("mainActivity", "요청 개수" + scopes.count())
                if (scopes.count() > 0) {
                    Log.d("mainActivity", "사용자에게 추가 동의를 받아야 합니다.")

                    UserApiClient.instance.loginWithNewScopes(context, scopes) { token, error ->
                        if (error != null) {
                            Log.d("mainActivity", "사용자 추가 동의 실패" + error)
                        } else {
                            Log.d("mainActivity", "allowed scopes: ${token!!.scopes}")

                            // 사용자 정보 재요청
                            UserApiClient.instance.me { user, error ->
                                if (error != null) {
                                    Log.d("mainActivity", "사용자 정보 요청 실패" + error)
                                } else if (user != null) {
                                    Log.d("mainActivity", "사용자 정보 요청 성공")
                                }
                            }
                        }
                    }
                }
            }
        }

        /////////////////////////////
        var birthdayArray: MutableList<User> = mutableListOf()

        birthdayArray.add(User("son", "0513"))
        birthdayArray.add(User("son1", "0614"))
        birthdayArray.add(User("son2", "1116"))
        birthdayArray.add(User("son3", "0714"))
        birthdayArray.add(User("son4", "0519"))
        birthdayArray.add(User("son5", "0615"))
        birthdayArray.add(User("son6", "0112"))
        birthdayArray.add(User("son7", "1019"))
        birthdayArray.add(User("son8", "0421"))
        birthdayArray.add(User("son9", "0305"))
        birthdayArray.add(User("son10", "0919"))

//        for (i in 0..9) {
//            birthdayArray.add(i, User("" + (i + 1), (i + 1) * 111111))
//        }

        val birthdayListObject: GetFriendsBirthday = GetFriendsBirthday()
        birthdayListObject.updateList(birthdayArray)
        birthdayListObject.getTodayBirth()

        Log.d("mainActivity", "birthdaylist: " + allBirthdayList)
        Log.d("mainActivity", "birthdaylist: " + todayBirthdayList)
        ///////////////////////////////


        val todayBirthDayadapter =
            MainTodayBirthdayAdapter(todayBirthdayList, LayoutInflater.from(this@MainActivity))
        findViewById<RecyclerView>(R.id.main_todayBirthday_recyclerview).adapter =
            todayBirthDayadapter
        findViewById<RecyclerView>(R.id.main_todayBirthday_recyclerview).layoutManager =
            LinearLayoutManager(this@MainActivity)

        //// 여기에 allBirthDay recyclerview 추가


//        if (friendsAgree == true) {
//            Log.d("mainActivity", "동의되어 있음")
//        } else {
//
//        }

//        val friendsAgree =

//        token_info_button.setOnClickListener {
//            // 토큰 정보 보기
//            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
//                if (error != null) {
//                    Log.d("login", "토큰 정보 보기 실패" + error)
//                } else if (tokenInfo != null) {
//                    Log.d(
//                        "login", "토큰 정보 보기 성공" +
//                                "\n회원번호: ${tokenInfo.id}" +
//                                "\n만료시간: ${tokenInfo.expiresIn} 초"
//                    )
//                }
//            }
//        }
//
//        user_info_button.setOnClickListener {
//            // 사용자 정보 요청 (기본)
//            UserApiClient.instance.me { user, error ->
//                if (error != null) {
//                    Log.d("login", "사용자 정보 요청 실패" + error)
//                } else if (user != null) {
//                    Log.d(
//                        "login", "사용자 정보 요청 성공" +
//                                "\n회원번호: ${user.id}" +
//                                "\n이메일: ${user.kakaoAccount?.email}" +
//                                "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
//                                "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
//                    )
//                }
//            }
//        }
//
//        logout_button.setOnClickListener {
//            UserApiClient.instance.logout { error ->
//                if (error != null) {
//                    Log.d("login", "로그아웃  실패. SDK에서 토큰 삭제됨" + error)
//                } else {
//                    Log.d("login", "로그아웃 성공. SDK에서 토큰 삭제됨")
//                }
//            }
//        }
//
        disconnect_button.setOnClickListener {
            // 연결 끊기
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Log.d("mainActivity", "연결 끊기 실패" + error)
                } else {
                    Log.d("mainActivity", "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                }
            }
        }

        getFriends_button.setOnClickListener {

//            TalkApiClient.instance.profile { profile, error ->
//                Log.d("mainActivity", profile?.nickname.toString())
//            }

            TalkApiClient.instance.friends { friends, error ->
                if (error != null) {
                    Log.d("mainActivity", "카카오톡 친구 목록 가져오기 실패" + error)
                } else if (friends != null) {
                    Log.d(
                        "mainActivity",
                        "카카오톡 친구 목록 가져오기 성공 \n${friends.elements?.joinToString("\n")}"
                    )
                    Log.d("mainActivity", "" + friends.elements?.size)

                    // 친구의 UUID 로 메시지 보내기 가능
                }
            }
        }


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

    