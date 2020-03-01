package com.coscos.ifthenplanner.Notification


import android.app.Notification
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import com.coscos.ifthenplanner.Database.AppDatabase
import com.coscos.ifthenplanner.Database.Plan
import com.coscos.ifthenplanner.PlanDetail
import com.coscos.ifthenplanner.R
import com.coscos.ifthenplanner.Reminder
import kotlinx.coroutines.runBlocking


class AlarmNotification : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        //変数の準備
        val channelId = "default"
        val title: String = intent!!.getStringExtra("title")!!
        val id = intent.getIntExtra("notificationID", 0)
        val content = intent.getStringExtra("then")

        val ifText = intent.getStringExtra("if")
        val monthString = intent.getStringExtra("month")
        val dateString = intent.getStringExtra("date")
        val dayStringRaw = intent.getStringExtra("day")
        val pMRaw = intent.getStringExtra("PM")
        val hourString = intent.getStringExtra("hour")
        val minString = intent.getStringExtra("minute")
        val colorInt = intent.getIntExtra("color", 0)

        val notifyIntent = Intent(context, Reminder::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        notifyIntent.putExtra("title", title)
        notifyIntent.putExtra("if", ifText)
        notifyIntent.putExtra("then", content)
        notifyIntent.putExtra("color", colorInt)
        notifyIntent.putExtra("month", monthString)
        notifyIntent.putExtra("date", dateString)
        notifyIntent.putExtra("day", dayStringRaw)
        notifyIntent.putExtra("PM", pMRaw)
        notifyIntent.putExtra("hour", hourString)
        notifyIntent.putExtra("minute", minString)
        notifyIntent.putExtra("madeAt", id)
        
        val notifyPendingIntent = PendingIntent.getActivity(
            context, id, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val longArray: LongArray = longArrayOf(100, 0, 100, 0, 100, 0)


        val notification = NotificationCompat.Builder(context!!, channelId)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.check_icon)
            .setContentText(content)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(notifyPendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVibrate(longArray)

        with(NotificationManagerCompat.from(context!!)) {
            notify(id, notification.build())
        }
    }
}