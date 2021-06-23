package org.application.birthday_notification.Fragment

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.application.birthday_notification.MainActivity
import org.application.birthday_notification.R
import org.application.birthday_notification.`object`.GetFriendsBirthday.Companion.allBirthdayList
import org.application.birthday_notification.alarm.AlarmReceiver
import org.application.birthday_notification.model.User
import org.application.birthday_notification.room.UserInfo
import org.application.birthday_notification.room.UserInfoDB
import java.text.SimpleDateFormat
import java.util.*

class BirthdayListFragment : Fragment() {
    lateinit var mContext: Context
    lateinit var mActivity: Activity

    private var userInfoDatabase: UserInfoDB? = null
    private var userInfoList: MutableList<UserInfo> =
        mutableListOf<UserInfo>() // room을 통해 받아온 db, 알림을 설정 내용 저장 X

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mContext = context
            mActivity = requireActivity()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_birthday_list, container, false)
        initView(view)

        return view
    }

    fun initView(view: View) {

        CoroutineScope(Dispatchers.IO).launch {
            userInfoDatabase = UserInfoDB.getInstance(mContext)
            userInfoList = userInfoDatabase?.userInfoDao()?.getAll()!!
            var sortedUserInfoList = userInfoList.sortedBy {
                it.user_birthday
            } as MutableList<UserInfo>

            CoroutineScope(Dispatchers.Main).launch {
                val allBirthDayAdapter =
                    BirthDayListFragmentAdapter(sortedUserInfoList,
                        LayoutInflater.from(mContext),
                        mContext,
                        mActivity)
                val allListRecyclerView: RecyclerView =
                    view.findViewById(R.id.fragment_birthday_all_birthday_recyclerview)
                allListRecyclerView.adapter = allBirthDayAdapter
                allListRecyclerView.layoutManager = LinearLayoutManager(mContext)
            }
        }
    }
}

class BirthDayListFragmentAdapter(
    val birthdayList: MutableList<UserInfo>,
    val inflater: LayoutInflater,
    val mContext: Context,
    val mActivity: Activity,
) : RecyclerView.Adapter<BirthDayListFragmentAdapter.ViewHolder>() {

    private var userInfoDatabase: UserInfoDB? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val friendsName: TextView
        val friendsBirthday: TextView
        val alarmCheckBox: CheckBox

        init {
            friendsName = itemView.findViewById(R.id.listitem_birthday_list_friendsName)
            friendsBirthday = itemView.findViewById(R.id.listitem_birthday_list_friendsBirthday)
            alarmCheckBox = itemView.findViewById(R.id.listitem_birthday_list_alarm_checkBox)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = inflater.inflate(R.layout.listitem_birthday_list, parent, false)
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.listitem_birthday_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("BirthdayFragment", "count" + birthdayList.size)
        holder.friendsName.setText(birthdayList.get(position).user_name)
        holder.friendsBirthday.setText(birthdayList.get(position).user_birthday)
        holder.alarmCheckBox.isChecked = birthdayList.get(position).alarm_set

//        if (holder.alarmCheckBox.isChecked) {
//            setAlarm(position, birthdayList.get(position).user_birthday)
//        }

        holder.alarmCheckBox.setOnClickListener {
            if (holder.alarmCheckBox.isChecked) {
                setAlarm(
                    birthdayList.get(position).id!!.toInt(),
                    birthdayList.get(position).user_birthday!!,
                    birthdayList.get(position).user_name!!
                )
                Log.d("BirthdayListFragment", "${birthdayList.get(position).user_name!!} 알람 설정 완료")
            } else {
                removeAlarm(birthdayList.get(position).id!!.toInt())
                Log.d("BirthdayListFragment", "${birthdayList.get(position).user_name!!} 알람 설정 취소")
            }
            Log.d("BirthdayListFragment", "" + holder.alarmCheckBox.isChecked)

            CoroutineScope(Dispatchers.IO).launch {
                userInfoDatabase = UserInfoDB.getInstance(mContext)

                val newUserInfo = UserInfo()
                newUserInfo.id = birthdayList.get(position).id
                newUserInfo.user_name = birthdayList.get(position).user_name
                newUserInfo.user_birthday = birthdayList.get(position).user_birthday
                newUserInfo.alarm_set = holder.alarmCheckBox.isChecked
                userInfoDatabase?.userInfoDao()?.insert(newUserInfo)
                birthdayList.get(position).alarm_set = !birthdayList.get(position).alarm_set
                Log.d("BirthdayListFragment",
                    "${birthdayList.get(position).user_name!!} alarm set is: " + holder.alarmCheckBox.isChecked)
            }

        }
    }

    override fun getItemCount(): Int {
        Log.d("BirthdayFragment", "count" + birthdayList.size)
        return birthdayList.size
    }

    fun setAlarm(id: Int, birthday: String, name: String) {
        var birthday_charArray = birthday?.toCharArray()
        var hour: String =
            birthday_charArray?.get(0).toString() + birthday_charArray?.get(1).toString()
        var minute: String =
            birthday_charArray?.get(2).toString() + birthday_charArray?.get(3).toString()

        Log.d("BirthdayListFragment", hour)
        Log.d("BirthdayListFragment", minute)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour.toInt())
            set(Calendar.MINUTE, minute.toInt())
        }

        val alarmManager = mActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(mContext, AlarmReceiver::class.java)
        intent.putExtra("id_number", id)
        intent.putExtra("birthday_user_name", name)
        intent.putExtra("birthday", birthday)

        val pendingIntent = PendingIntent.getBroadcast(mContext,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun removeAlarm(id: Int) {
        val intent = Intent(mContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(mContext,
            id,
            intent,
            PendingIntent.FLAG_NO_CREATE)
        pendingIntent?.cancel()
    }
}