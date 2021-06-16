package org.application.birthday_notification

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val context = this@LoginActivity

        val kakao_login_button: Button = findViewById(R.id.kakao_login_button)

        //////////////////////////////////////////////////
        // 여기에 추가할 사항
        // sharedperference 를 이용해서 accessToken 저장
        // accessToken 기간이 만료되면 자동 리프레쉬?
        //////////////////////////////////////////////////

//        val sharedPreference = getSharedPreferences("Login", Context.MODE_PRIVATE)
//        val token = sharedPreference.getString("accessToken", "no token")


        // 로그인 공통 callback 구성
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.d("login", "로그인 실패" + error)
            } else if (token != null) {
                Log.d("login", "로그인 성공 ${token.accessToken}")
                startMainActivity(token.accessToken)
                finish()
            }
        }

        kakao_login_button.setOnClickListener {
            Log.d("login", "실행")
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
//            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
//                UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
//            } else {
//                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
//            }


            // 카카오계정으로 로그인
            UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                if (error != null) {
//                    Log.e(TAG, "로그인 실패", error)
                } else if (token != null) {
//                    Log.i(TAG, "로그인 성공 ${token.accessToken}")
                    finish()
                    startMainActivity(token.accessToken)
                }
            }
        }

    }

    private fun startMainActivity(accessToken: String) {
        // sharedPreference 에 accessToken 추가
//        val sharedPreference = getSharedPreferences("Login", Context.MODE_PRIVATE)
//        val editor: SharedPreferences.Editor = sharedPreference.edit()
//        editor.putString("accessToken", accessToken)
//        editor.commit()

        // MainActivity 실행
        startActivity(Intent(this, MainActivity::class.java))
    }

}
