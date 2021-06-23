package org.application.birthday_notification

import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.application.birthday_notification.alarm.AlarmReceiver
import java.util.*


class alarmService : Service() {
    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // PendingIntent를 이용하면 포그라운드 서비스 상태에서 알림을 누르면 앱의 MainActivity를 다시 열게 된다.
//        val testIntent = Intent(ApplicationProvider.getApplicationContext<Context>(),
//            MainActivity::class.java)
//        val pendingIntent =
//            PendingIntent.getActivity(this, 0, testIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        Log.d("alarmService", "실행")

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 17)
            set(Calendar.MINUTE, 52)
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this@alarmService, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this@alarmService,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )


        val calendar1 = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 40)
        }

        val alarmManager1 = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent1 = Intent(this@alarmService, AlarmReceiver::class.java)
        val pendingIntent1 = PendingIntent.getBroadcast(this@alarmService,
            2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager1.setInexactRepeating(AlarmManager.RTC_WAKEUP,
            calendar1.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent1
        )

        // 오래오 윗버젼일 때는 아래와 같이 채널을 만들어 Notification과 연결해야 한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val service_notification_channel =
                NotificationChannel("service_channel", "birthday_notify",
                    NotificationManager.IMPORTANCE_LOW)


            NotificationManagerCompat.from(applicationContext)
                .createNotificationChannel(service_notification_channel)

            with(NotificationManagerCompat.from(applicationContext)) {
                val build = NotificationCompat.Builder(applicationContext,
                    "service_channel")
                    .setContentTitle("BIRTHDAY_NOTI")
                    .setContentText("생일 알림이 실행중입니다.")
                    .setSmallIcon(org.application.birthday_notification.R.drawable.ic_launcher_foreground)
                    .setPriority(NotificationCompat.PRIORITY_LOW)


                notify(3000, build.build())
                startForeground(3000, build.build())
            }

//            // Notification과 채널 연걸
//            val mNotificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
//            mNotificationManager!!.createNotificationChannel(service_notification_channel)
//
//            // Notification 세팅
//            val notification: NotificationCompat.Builder = NotificationCompat.Builder(
//                ApplicationProvider.getApplicationContext<Context>(),
//                "channel")
//                .setSmallIcon(R.drawable.round_account_box_24)
//                .setContentTitle("현재 실행 중인 앱 이름")
//                .setContentIntent(pendingIntent)
//                .setContentText("")

            // id 값은 0보다 큰 양수가 들어가야 한다.
//            mNotificationManager.notify(1, notification.build())
            // foreground에서 시작
//            startForeground(1, notification.build())
        }

        return START_REDELIVER_INTENT
    }
}