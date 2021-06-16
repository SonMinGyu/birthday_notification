package org.application.birthday_notification

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private val delayTime: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        CoroutineScope(Dispatchers.IO).launch {
            delay(delayTime)

            if (AuthApiClient.instance.hasToken()) {
                UserApiClient.instance.accessTokenInfo { _, error ->
                    if (error != null) {
                        if (error is KakaoSdkError && error.isInvalidTokenError() == true) {
                            //로그인 필요
                            Log.d("splachActivity", "hasToken 로그인 필요")
                            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            //기타 에러
                            Log.d("splachActivity", "hasToken 에러발생")
                        }
                    } else {
                        Log.d("splachActivity", "hasToken 로그인 필요X")
                        //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                Log.d("splachActivity", "noToken 로그인 필요")
                //로그인 필요
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }


//            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
//            startActivity(intent)
//            finish()

        }
    }
}