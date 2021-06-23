package org.application.birthday_notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import org.application.birthday_notification.alarm.AlarmReceiver
import java.util.*

class MyService : Service() {
    companion object {
        const val NOTIFICATION_ID = 10
        const val CHANNEL_ID = "primary_notification_channel"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("Test", "MyService is started")

//        for (i in 1 until 4) {
//            val calendar = Calendar.getInstance().apply {
//                set(Calendar.HOUR_OF_DAY, 13)
//                set(Calendar.MINUTE, 36+i)
//            }
//
//            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//            val intent = Intent(this@MyService, AlarmReceiver::class.java)
//            val pendingIntent = PendingIntent.getBroadcast(this@MyService,
//                i,
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT)
//
//            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//                calendar.timeInMillis,
//                AlarmManager.INTERVAL_DAY,
//                pendingIntent
//            )
//        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MyService is running")
                .setContentText("MyService is running")
                .build()
            Log.d("Test", "start foreground")
            startForeground(NOTIFICATION_ID, notification)
        }
    }

//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//
//        val calendar = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, 18)
//            set(Calendar.MINUTE, 5)
//        }
//
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(this@MyService, AlarmReceiver::class.java)
//
//        val pendingIntent = PendingIntent.getBroadcast(this@MyService,
//            1,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT)
//
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_DAY,
//            pendingIntent
//        )
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createNotificationChannel()
//            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
////                .setContentTitle("MyService is running")
////                .setContentText("MyService is running")
////                .setSmallIcon(R.drawable.ic_launcher_foreground)
////                .setPriority(NotificationCompat.PRIORITY_LOW)
//                .build()
//            Log.d("Test", "start foreground")
//            startForeground(NOTIFICATION_ID, notification)
//        }
//
//        return START_STICKY
//    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            "MyApp notification",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = "AppApp Tests"

        val notificationManager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            notificationChannel)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}