package com.coscos.ifthenplanner

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import com.coscos.ifthenplanner.Database.AppDatabase
import com.coscos.ifthenplanner.Notification.AlarmNotification
import kotlinx.android.synthetic.main.activity_option.*
import kotlinx.android.synthetic.main.activity_option.toolbar
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.provider.Settings.*
import kotlinx.coroutines.*


class Option : AppCompatActivity() {

    private val job = Job()
    lateinit var dataStore: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)

        dataStore = getSharedPreferences("DataStore", Context.MODE_PRIVATE)

        val notificationBoolean = dataStore.getBoolean("notificationNullCheck", false)
        if (notificationBoolean) {
            switch1.isChecked = true
        }

        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        } ?: IllegalAccessException("Toolbar cannot be null")
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    //すべて削除
    fun clearAll(view: View) {
        AlertDialog.Builder(this) // FragmentではActivityを取得して生成
            .setTitle(R.string.clear)
            .setMessage(R.string.ask_if)
            .setPositiveButton("OK") { dialog, which ->
                CoroutineScope(Dispatchers.Main + job).launch {
                    val db = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java, "planDatabase"
                    ).build()
                    val dao = db.PlanDao()
                    val plans = dao.loadAllPlan()
                    dao.deleteAll(plans)

                }
            }
            .setNegativeButton("キャンセル") { dialog, which -> }
            .setIcon(R.drawable.ic_delete_black_24dp)
            .show()
    }

    //通知を削除する関数
    private fun deleteNotification(madeAtList: MutableList<String>, pos: Int) {
        val am: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val id = madeAtList[pos].toInt()
        val notificationIntent = Intent(applicationContext, AlarmNotification::class.java)
        val pending = PendingIntent.getBroadcast(
            applicationContext, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        pending.cancel()
        am.cancel(pending)
    }

    //タグの詳細設定
    fun tagOption(view: View) {
        val intent = Intent(this@Option, ColorTagOption::class.java)
        startActivity(intent)
    }

    //リマインダーを無効にする
    @SuppressLint("CommitPrefEdits")
    fun reminderNull(view: View) {
        val ifCheckd: Boolean = switch1.isChecked
        if (ifCheckd) {
            editor = dataStore.edit()

            editor.putBoolean("notificationNullCheck", false)

            editor.apply()
            switch1.isChecked = false
        } else {
            editor = dataStore.edit()

            editor.putBoolean("notificationNullCheck", true)

            editor.apply()
            switch1.isChecked = true
        }
    }

    //セットされたリマインダーを削除する
    fun clearReminder(view: View) {
        AlertDialog.Builder(this) // FragmentではActivityを取得して生成
            .setTitle(R.string.clear)
            .setMessage(R.string.ask_if_notification)
            .setPositiveButton("OK") { dialog, which ->
                CoroutineScope(Dispatchers.Main + job).launch {
                    val db = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java, "planDatabase"
                    ).build()
                    val dao = db.PlanDao()
                    val plans = dao.loadAllPlan()
                    val madeAtList: MutableList<String> = mutableListOf()
                    for (i in plans.indices) {
                        madeAtList.add(plans[i].madeAt)
                    }
                    for (i in madeAtList.indices) {
                        deleteNotification(madeAtList, i)
                    }
                    dao.deleteNotification(false)
                }
            }
            .setNegativeButton("キャンセル") { dialog, which -> }
            .setIcon(R.drawable.ic_delete_black_24dp)
            .show()
    }

    //通知の設定
    fun notificationOption(view: View) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + BuildConfig.APPLICATION_ID))
            // カテゴリは設定しなくてもいいかも
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            // Flagは好みで設定
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            startActivity(intent)
        } else {
            val intent = Intent(ACTION_APP_NOTIFICATION_SETTINGS)
            // カテゴリは設定しなくてもいいかも
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            // Flagは好みで設定
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

            // for Android 5-7
            intent.putExtra("app_package", BuildConfig.APPLICATION_ID)
            intent.putExtra("app_uid", applicationContext.applicationInfo.uid)

            // for Android O
            intent.putExtra("android.provider.extra.APP_PACKAGE", BuildConfig.APPLICATION_ID)

            startActivity(intent)
        }
    }
}
