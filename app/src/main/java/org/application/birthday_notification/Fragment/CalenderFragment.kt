package org.application.birthday_notification.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import org.application.birthday_notification.Fragment.CalenderFragment.Companion.pagePosition
import org.application.birthday_notification.MainActivity
import org.application.birthday_notification.R
import org.application.birthday_notification.`object`.GetFriendsBirthday
import org.application.birthday_notification.`object`.GetFriendsBirthday.Companion.clickedBirthdayList
import org.application.birthday_notification.model.User
import java.text.SimpleDateFormat
import java.util.*

class CalenderFragment() : Fragment() {
    lateinit var calendarViewPager: ViewPager2
    lateinit var calendar_year_month_text: TextView
    lateinit var calendar_day_text: TextView
    lateinit var calendar_recycler_view: RecyclerView
    lateinit var mContext: Context

    companion object {
        var pagePosition: Int? = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mContext = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_calender, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        initView()

        calendar_year_month_text = view.findViewById(R.id.fragment_calendar_year_month_text)
        calendar_day_text = view.findViewById(R.id.fragment_calendar_day_text)
        calendar_recycler_view =
            view.findViewById(R.id.fragment_calendar_clicked_list_recycler_view)


        val clickedBirthDayAdapter =
            CalendarListFragmentAdapter(GetFriendsBirthday.clickedBirthdayList)
        calendar_recycler_view.adapter = clickedBirthDayAdapter
        calendar_recycler_view.layoutManager = LinearLayoutManager(mContext)


        val firstFragmentStateAdapter =
            FirstFragmentStateAdapter(requireActivity(),
                calendar_day_text,
                clickedBirthDayAdapter,
                calendar_recycler_view)
        calendarViewPager = view?.findViewById(R.id.fragment_calendar_viewpager)
        calendarViewPager?.adapter = firstFragmentStateAdapter
        calendarViewPager?.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        calendarViewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (positionOffsetPixels == 0) {
                    var pageIndex = (position - (Int.MAX_VALUE / 2))
                    val date = Calendar.getInstance().run {
                        add(Calendar.MONTH, pageIndex)
                        time
                    }

                    val currentDate: Date = date
//                    Log.e(TAG, "$date")
                    // 포맷 적용
                    var datetime: String = SimpleDateFormat(
                        "yyyy년 MM월",
                        Locale.KOREA
                    ).format(date.time)
                    calendar_year_month_text.setText(datetime)

                    calendar_day_text.setText("")

                    clickedBirthdayList.clear()
                    clickedBirthDayAdapter.notifyDataSetChanged()
                }
            }
        })

        firstFragmentStateAdapter.apply {
            calendarViewPager?.setCurrentItem(this.firstFragmentPosition, false)
        }
    }

//    fun initView() {
//        val firstFragmentStateAdapter = FirstFragmentStateAdapter(requireActivity())
//        val calendarViewPager: ViewPager2? = view?.findViewById(R.id.fragment_calendar_viewpager)
//        calendarViewPager?.adapter = firstFragmentStateAdapter
//        calendarViewPager?.orientation = ViewPager2.ORIENTATION_HORIZONTAL
//
//        firstFragmentStateAdapter.apply {
//            calendarViewPager?.setCurrentItem(this.firstFragmentPosition, false)
//        }
//    }


    override fun onResume() {
        super.onResume()

        initPosition(calendarViewPager)
    }

    fun initPosition(calendarViewPager: ViewPager2) {
        calendarViewPager.setCurrentItem(Int.MAX_VALUE / 2)
    }


}

class FirstFragmentStateAdapter(
    fragmentActivity: FragmentActivity,
    val calendar_day_text: TextView,
    val clickedBirthDayAdapter: CalendarListFragmentAdapter,
    val calendar_recycler_view: RecyclerView,
) :
    FragmentStateAdapter(fragmentActivity) {

    private val pageCount = Int.MAX_VALUE
    val firstFragmentPosition = Int.MAX_VALUE / 2

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun createFragment(position: Int): Fragment {
        val calendarViewpagerFragment = CalendarViewpagerFragment(calendar_day_text,
            clickedBirthDayAdapter,
            calendar_recycler_view)
        calendarViewpagerFragment.pageIndex = position
        pagePosition = position
        return calendarViewpagerFragment
    }

}


class CalendarListFragmentAdapter(
    val birthdayList: MutableList<User>,
) : RecyclerView.Adapter<CalendarListFragmentAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val friendsName: TextView
        val friendsBirthday: TextView

        init {
            friendsName = itemView.findViewById(R.id.listitem_calendar_birthday_list_friendsName)
            friendsBirthday =
                itemView.findViewById(R.id.listitem_calendar_birthday_list_friendsBirthday)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.listitem_calendar_birthday_list, parent, false)
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