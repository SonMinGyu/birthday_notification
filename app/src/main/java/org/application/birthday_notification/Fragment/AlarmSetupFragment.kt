package org.application.birthday_notification.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.kakao.sdk.user.UserApiClient
import org.application.birthday_notification.LoginActivity
import org.application.birthday_notification.MainActivity
import org.application.birthday_notification.R
import android.content.Intent as Intent1

class AlarmSetupFragment() : Fragment() {

    lateinit var logoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
//

//        Log.d("life_cycle", "F onCreateView $lay")
        return inflater.inflate(R.layout.fragment_alarm_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        logoutButton = view.findViewById(R.id.fragment_alarm_setup_logout_button)
        logoutButton.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.d("AlarmSetupFragment", "로그아웃 실패. SDK에서 토큰 삭제됨: " + error)
                } else {
                    Log.d("AlarmSetupFragment", "로그아웃 성공. SDK에서 토큰 삭제됨")
                }
            }

            requireActivity().finish()
            startActivity(Intent1(requireActivity(), LoginActivity::class.java))
        }

    }
}