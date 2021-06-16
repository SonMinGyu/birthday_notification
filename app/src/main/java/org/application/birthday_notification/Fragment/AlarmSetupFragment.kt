package org.application.birthday_notification.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.application.birthday_notification.R

class AlarmSetupFragment() : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        Log.d("life_cycle", "F onCreateView $lay")
        return inflater.inflate(R.layout.fragment_alarm_setup, container, false)
    }
}