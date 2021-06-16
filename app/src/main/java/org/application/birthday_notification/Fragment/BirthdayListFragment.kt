package org.application.birthday_notification.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.application.birthday_notification.MainActivity
import org.application.birthday_notification.R
import org.application.birthday_notification.`object`.GetFriendsBirthday.Companion.allBirthdayList
import org.application.birthday_notification.model.User
import java.text.SimpleDateFormat
import java.util.*

class BirthdayListFragment : Fragment() {
    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mContext = context
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
        val allBirthDayAdapter =
            BirthDayListFragmentAdapter(allBirthdayList, LayoutInflater.from(mContext))
        val allListRecyclerView: RecyclerView =
            view.findViewById(R.id.fragment_birthday_all_birthday_recyclerview)
        allListRecyclerView.adapter = allBirthDayAdapter
        allListRecyclerView.layoutManager = LinearLayoutManager(mContext)
    }
}

class BirthDayListFragmentAdapter(
    val birthdayList: MutableList<User>,
    val inflater: LayoutInflater,
) : RecyclerView.Adapter<BirthDayListFragmentAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val friendsName: TextView
        val friendsBirthday: TextView

        init {
            friendsName = itemView.findViewById(R.id.listitem_birthday_list_friendsName)
            friendsBirthday = itemView.findViewById(R.id.listitem_birthday_list_friendsBirthday)
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
        holder.friendsName.setText(birthdayList.get(position).name)
        holder.friendsBirthday.setText(birthdayList.get(position).birthday)

    }

    override fun getItemCount(): Int {
        Log.d("BirthdayFragment", "count" + birthdayList.size)
        return birthdayList.size
    }
}