package com.coscos.ifthenplanner.Notification


import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
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

        val channelId = "default"
        val title: String = context!!.getString(R.string.app_name)
        val id = intent!!.getIntExtra("notificationID", 0)
        val content = intent.getStringExtra("content")

        //var plan: Plan? =null


        /*val resultIntent = Intent(context, PlanDetail::class.java)*/
        /*runBlocking {
            val db = Room.databaseBuilder(
                context,
                AppDatabase::class.java, "planDatabase"
            ).build()
            val dao = db.PlanDao()
            plan = dao.loadAt(id.toString())
        }*/

        /*plan = Plan("test", "if", "then", 2, true, "2020", "1", "29", "Fri", "PM", "3", "45", "201910151130")

        resultIntent.putExtra("position", 0)
        resultIntent.putExtra("if_it_is_edit", false)
        resultIntent.putExtra("title", plan!!.titleText)
        resultIntent.putExtra("if", plan!!.ifText)
        resultIntent.putExtra("then", plan!!.thenText)
        resultIntent.putExtra("color", plan!!.colorInt)
        resultIntent.putExtra("notification", plan!!.isNotificationTrue)
        resultIntent.putExtra("year", plan!!.yearString)
        resultIntent.putExtra("month", plan!!.monthString)
        resultIntent.putExtra("date", plan!!.dateString)
        resultIntent.putExtra("day", plan!!.dayStringRaw)
        resultIntent.putExtra("PM", plan!!.pMRaw)
        resultIntent.putExtra("hour", plan!!.hourString)
        resultIntent.putExtra("minute", plan!!.minString)
        resultIntent.putExtra("madeAt", plan!!.madeAt)*/

        //resultIntent.putExtra()
        /*val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }*/

        val notifyIntent = Intent(context, Reminder::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val notifyPendingIntent = PendingIntent.getActivity(
            context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )



        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setSmallIcon(com.coscos.ifthenplanner.R.drawable.check_icon)
            .setContentText(content)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(notifyPendingIntent)
            .setWhen(System.currentTimeMillis())


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(channelId)
        }

        with(NotificationManagerCompat.from(context)) {
            notify(id, notification.build())
        }
    }
}