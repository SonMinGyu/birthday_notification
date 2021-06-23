package org.application.birthday_notification.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import org.application.birthday_notification.MainActivity
import org.application.birthday_notification.R
import org.application.birthday_notification.`object`.GetFriendsBirthday
import org.application.birthday_notification.`object`.GetFriendsBirthday.Companion.notification_number
import org.application.birthday_notification.`object`.GetFriendsBirthday.Companion.position_number


class AlarmReceiver : BroadcastReceiver() {

    private var getObject: GetFriendsBirthday = GetFriendsBirthday()

    companion object {
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "1000"
    }

    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager

        var id_n = intent.getIntExtra("id_number", 90000)
        var birthday_user_name: String? = intent.getStringExtra("birthday_user_name")
        var birthday: String? = intent.getStringExtra("birthday")

        Log.d("AlarmReceiver", "id_n: " + id_n)
        // intent로 전달받아서 뿌려주자

        createNotificationChannel(context)
        notifyNotification(context, birthday_user_name, birthday)

    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "생일 알림",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(
                notificationChannel)

//            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel) // 수정1
        }
    }

    private fun notifyNotification(context: Context, name: String?, birthday: String?) {
//        with(NotificationManagerCompat.from(context)) {
//            val build = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
//                .setContentTitle("알림")
//                .setContentText("오늘은 님의 생일 입니다!")
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setPriority(NotificationCompat.PRIORITY_LOW)
//
//
//            notify(NOTIFICATION_ID, build.build())
//        } // 수정1

        val now: Int = System.currentTimeMillis().toInt()

        val contentIntent = Intent(context, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            now,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("${birthday} 생일 알림")
                .setContentText("오늘은 ${name} 님의 생일입니다! 생일을 축하해주세요!")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
//                .setAutoCancel(true)
//                .setDefaults(NotificationCompat.DEFAULT_ALL)

        notificationManager.notify(now, builder.build())

        getObject.plusNotificationNumber()
    }
}