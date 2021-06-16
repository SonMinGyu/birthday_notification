package org.application.birthday_notification.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.application.birthday_notification.Fragment.CalenderFragment.Companion.pagePosition
import org.application.birthday_notification.MainActivity
import org.application.birthday_notification.R
import org.application.birthday_notification.Util.MyCalendar
import org.application.birthday_notification.`object`.GetFriendsBirthday
import org.application.birthday_notification.`object`.GetFriendsBirthday.Companion.allBirthdayList
import org.application.birthday_notification.`object`.GetFriendsBirthday.Companion.clickedBirthdayList
import org.application.birthday_notification.model.User
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

lateinit var clickedMonth: String
lateinit var clickedYear: String

//lateinit var clickedBirthDayAdapter: CalendarListFragmentAdapter
//lateinit var recyclerAdapter: CalendarAdapter

//lateinit var calendar_year_month_text: TextView

val calendarViewBirthdayListObject: GetFriendsBirthday = GetFriendsBirthday()

class CalendarViewpagerFragment(val calendar_day_text: TextView) : Fragment() {

    private val TAG = javaClass.simpleName
    lateinit var mContext: Context
    lateinit var calendarClieckedListRecyclerView: RecyclerView
    lateinit var calendar_recyclerview: RecyclerView

    var pageIndex = 0
    lateinit var currentDate: Date

    lateinit var calendar_layout: LinearLayout
    lateinit var calendar_view: RecyclerView
//    lateinit var calendar_year_month_text_view: TextView

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
        val view: View = inflater.inflate(R.layout.fragment_calendar_viewpager, container, false)
        initView(view)

        Log.d("CalendarViewPager", "onCreateView")

        return view
//        return inflater.inflate(R.layout.fragment_calendar_viewpager, container, false)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.d("CalendarViewPager", "onPause")
    }


    fun initView(view: View) {
        pageIndex -= (Int.MAX_VALUE / 2)
        Log.e(TAG, "Calender Index: $pageIndex")
//        calendar_year_month_text_view = calendar_year_month_text
        calendar_layout = view.findViewById(R.id.fragment_calendar_layout)
        calendar_view = view.findViewById(R.id.fragment_calendar_recycler_view)

        // 날짜 적용
        val date = Calendar.getInstance().run {
            add(Calendar.MONTH, pageIndex)
            time
        }

        currentDate = date
        Log.e(TAG, "$date")
        // 포맷 적용
        var datetime: String = SimpleDateFormat(
            "yyyy년 MM월 dd일",
            Locale.KOREA
        ).format(date.time)
//        calendar_year_month_text_view.setText(datetime)

        // 달력 클릭시 클릭한 날이 몇월 인지 알기위해 저장
        clickedMonth = SimpleDateFormat(
            "MM",
            Locale.KOREA
        ).format(date.time)

        // 달력 클릭시 클릭한 날이 몇월 인지 알기위해 저장
        clickedYear = SimpleDateFormat(
            "yyyy",
            Locale.KOREA
        ).format(date.time)


        val clickedBirthDayAdapter =
            CalendarListFragmentAdapter(clickedBirthdayList)
        val calendarClieckedListRecyclerView: RecyclerView =
            view.findViewById(R.id.fragment_calendar_clicked_list_recycler_view)
        calendarClieckedListRecyclerView.adapter = clickedBirthDayAdapter
        calendarClieckedListRecyclerView.layoutManager = LinearLayoutManager(mContext)
//        calendarClieckedListRecyclerView =
//            view.findViewById(R.id.fragment_calendar_clicked_list_recycler_view)


        val recyclerAdapter =
            CalendarAdapter(
                mContext,
                calendar_layout,
                currentDate,
                clickedBirthDayAdapter,
                calendar_day_text,
            )
        val calendar_recyclerview: RecyclerView =
            view.findViewById(R.id.fragment_calendar_recycler_view)
        calendar_recyclerview.adapter = recyclerAdapter
        calendar_recyclerview.layoutManager = GridLayoutManager(mContext, 7)

//        calendar_recyclerview = view.findViewById(R.id.fragment_calendar_recycler_view)
    }
}

class CalendarAdapter(
    val context: Context,
    val calendarLayout: LinearLayout,
    val date: Date,
    val clickedBirthDayAdapter: CalendarListFragmentAdapter,
    val calendar_day_text: TextView,
) :
    RecyclerView.Adapter<CalendarAdapter.CalendarItemHolder>() {

    private val TAG = javaClass.simpleName
    var dataList: ArrayList<Int> = arrayListOf()

    // FurangCalendar을 이용하여 날짜 리스트 세팅
    var furangCalendar: MyCalendar = MyCalendar(date)

    init {
        furangCalendar.initBaseCalendar()
        dataList = furangCalendar.dateList
    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onBindViewHolder(holder: CalendarItemHolder, position: Int) {

        // list_item_calendar 높이 지정
        val h = calendarLayout.height / 6
        holder.itemView.layoutParams.height = h

        holder?.bind(dataList[position], position, context)
        if (itemClick != null) {
            holder?.itemView?.setOnClickListener { v ->
                itemClick?.onClick(v, position)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarItemHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.listitem_calendar, parent, false)
        return CalendarItemHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    inner class CalendarItemHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var itemCalendarDateText: TextView =
            itemView!!.findViewById(R.id.listitem_calendar_date_text)

        fun bind(data: Int, position: Int, context: Context) {
            val firstDateIndex = furangCalendar.prevTail
            val lastDateIndex = dataList.size - furangCalendar.nextHead - 1

            // 날짜 표시
            itemCalendarDateText.setText(data.toString())

            // 오늘 날짜 처리
            var dateString: String = SimpleDateFormat("dd", Locale.KOREA).format(date)
            var dateInt = dateString.toInt()

            // 현재 월의 1일 이전, 현재 월의 마지막일 이후 값의 텍스트를 회색처리
            if (position < firstDateIndex || position > lastDateIndex) {
                itemCalendarDateText.setTextAppearance(R.style.LightGray)
                itemCalendarDateText.background = null
            } else {
                itemCalendarDateText.setTextAppearance(R.style.white)

                var dayString: String = ""

                if (data.toString().length == 1) {
                    dayString = "0" + data.toString()
                } else {
                    dayString = data.toString()
                }

                var MD: String = clickedMonth + dayString

                for (i in 0 until allBirthdayList.size) {
                    if (allBirthdayList.get(i).birthday == MD) {
                        itemCalendarDateText.setBackgroundColor(R.style.TODAY_COLOR)
                    }
                }

                // calendar 아래 recyclerview 업데이트
                var clickedDayString: String =
                    clickedYear + "년 " + clickedMonth + "월 " + dayString + "일"

                itemCalendarDateText.setOnClickListener {
                    Log.d("CalendarViewPager", MD)
                    calendarViewBirthdayListObject.cleanClickedBirthList()
                    calendarViewBirthdayListObject.getClickedBirth(MD)
                    clickedBirthDayAdapter.notifyDataSetChanged()
                    Log.d("CalendarViewPager", "list" + clickedBirthdayList)

                    calendar_day_text.setText(dayString + "일")
                }

                if (dataList[position] == dateInt && pagePosition == (Int.MAX_VALUE / 2)) {
//                itemCalendarDateText.setTypeface(itemCalendarDateText.typeface, Typeface.BOLD)
                    itemCalendarDateText.setTextAppearance(R.style.BOLD)
                    itemCalendarDateText.setTextColor(ContextCompat.getColor(context,
                        R.color.TODAY))
                }
            }
        }
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
