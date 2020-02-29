package com.coscos.ifthenplanner

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import android.os.AsyncTask
import android.os.Build
import android.provider.Settings
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import com.coscos.ifthenplanner.Database.AppDatabase
import com.coscos.ifthenplanner.Database.Plan
import com.coscos.ifthenplanner.Database.PlanDao
import kotlinx.coroutines.*
import android.view.Gravity.END
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import com.coscos.ifthenplanner.Adapter.RecyclerAdapter
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.coscos.ifthenplanner.Notification.AlarmNotification
import java.util.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    //データベース
    var plans: Array<Plan> = emptyArray()

    private val job = Job()

    //RecyclerViewに用いるリストを定義
    var titleList: MutableList<String> = mutableListOf()
    var ifList: MutableList<String> = mutableListOf()
    var thenList: MutableList<String> = mutableListOf()
    var colorList: MutableList<Int> = mutableListOf()

    //通知情報のリスト
    var notificationBooleanList: MutableList<Boolean> = mutableListOf()
    var yearList: MutableList<String> = mutableListOf()
    var monthList: MutableList<String> = mutableListOf()
    var dateList: MutableList<String> = mutableListOf()
    var dayList: MutableList<String> = mutableListOf()
    var pMList: MutableList<String> = mutableListOf()
    var hourList: MutableList<String> = mutableListOf()
    var minList: MutableList<String> = mutableListOf()

    var madeAtList: MutableList<String> = mutableListOf()


    //定数を定義
    companion object {
        const val NEW_PLAN: Int = 1
        const val DETAIL_PLAN: Int = 2
        const val DELETE: Int = 3
        const val RESTART: Int = 4
        const val EDIT: Int = 5

        const val CHANNEL_ID: String = "default"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //通知チャネルの作成
        createNotificationChannel()

        //アクションバーの設定
        setSupportActionBar(findViewById(R.id.toolbar))

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        //リサイクラービューを取得
        recyclerView = findViewById(R.id.recycler_view)

        //データベースからデータを取得
        CoroutineScope(Dispatchers.Main + job).launch {
            plans = startDB().loadAllPlan()

            if (plans.isNotEmpty()) {
                for (i in plans.indices) {
                    titleList.add(plans[i].titleText)
                    ifList.add(plans[i].ifText)
                    thenList.add(plans[i].thenText)
                    colorList.add(plans[i].colorInt)
                    notificationBooleanList.add(plans[i].isNotificationTrue)
                    yearList.add(plans[i].yearString)
                    monthList.add(plans[i].monthString)
                    dateList.add(plans[i].dateString)
                    dayList.add(plans[i].dayStringRaw)
                    pMList.add(plans[i].pMRaw)
                    hourList.add(plans[i].hourString)
                    minList.add(plans[i].minString)
                    madeAtList.add(plans[i].madeAt)
                }
            }
        }

        CoroutineScope(Dispatchers.Main + job).launch {
            delay(50)
            if (ifList.isEmpty()) {
                recyclerView.visibility = View.GONE
                empty_view.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                empty_view.visibility = View.GONE

                val viewManager = LinearLayoutManager(applicationContext)
                val viewAdapter = RecyclerAdapter(
                    applicationContext,
                    titleList,
                    ifList,
                    thenList,
                    colorList
                )

                setAdapterListener(viewAdapter)

                recyclerView.layoutManager = viewManager
                recyclerView.adapter = viewAdapter
            }
        }
    }

    //PlanDaoをリターンする関数
    private fun startDB(): PlanDao {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "planDatabase"
        ).build()
        return db.PlanDao()
    }

    //データベースの非同期処理
    inner class myAsyncTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            val dao = startDB()
            if (deleteLater != null) {
                dao.deletePlan(deleteLater.toString())
                deleteLater = null
            }
            if (addLater != null) {
                //追加する処理
                dao.insertPlan(addLater!!)
                addLater = null
            }
            if (changeLaterIF != null) {
                dao.updatePlan(
                    changeLaterTITLE!!, changeLaterIF!!, changeLaterTITLE!!,
                    changeLaterCOLOR!!, changeLaterNOTIF!!, changeLaterYEAR!!,
                    changeLaterMONTH!!, changeLaterDATE!!, changeLaterDAY!!,
                    changeLaterPM!!, changeLaterHOUR!!, changeLaterMIN!!, changeLaterMAL!!
                )
            }
            return null
        }
    }


    //データベースの処理。myAsyncTaskを実行
    override fun onStop() {
        super.onStop()
        myAsyncTask().execute()
    }


    //ライフサイクルのラスト
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    //オプションメニュー作成
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    //オプションメニューアイテムのリスナ
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_all -> {
                if (ifList.isEmpty()) {
                    Toast.makeText(applicationContext, "削除するプランがありません", Toast.LENGTH_SHORT).show()

                } else {
                    AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                        .setTitle(R.string.clear)
                        .setMessage(R.string.ask_if)
                        .setPositiveButton("OK") { dialog, which ->
                            CoroutineScope(Dispatchers.Main + job).launch {
                                val dao = startDB()
                                val plans = dao.loadAllPlan()
                                dao.deleteAll(plans)
                                titleList.clear()
                                ifList.clear()
                                thenList.clear()
                                colorList.clear()
                                notificationBooleanList.clear()
                                yearList.clear()
                                monthList.clear()
                                dateList.clear()
                                dayList.clear()
                                pMList.clear()
                                hourList.clear()
                                minList.clear()
                                madeAtList.clear()

                                recyclerView.visibility = View.GONE
                                empty_view.visibility = View.VISIBLE
                            }
                        }
                        .setNegativeButton("キャンセル") { dialog, which -> }
                        .setIcon(R.drawable.ic_delete_black_24dp)
                        .show()
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    //fabのオンクリック
    fun addPlan(view: View) {

        val calendar1 = Calendar.getInstance()
        calendar1.setTimeInMillis(System.currentTimeMillis())

        calendar1.add(Calendar.SECOND, 1)
        val notificationIntent = Intent(applicationContext, AlarmNotification::class.java)
        notificationIntent.putExtra("notificationID", /*madeAtList[0].toInt()*/ 0)
        notificationIntent.putExtra("content", "テスト")

        val pending = PendingIntent.getBroadcast(
            applicationContext, 10, notificationIntent, 0
        )

        val am: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        am.setExact(AlarmManager.RTC_WAKEUP, calendar1.timeInMillis, pending)






        val intent = Intent(this@MainActivity, NewPlan::class.java)
        startActivityForResult(intent, NEW_PLAN)
    }

    //メインアクティビティに戻ってきたときの処理
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == NEW_PLAN) {

            if (resultCode == RESULT_OK) {
                val ifContent = data!!.getStringExtra("if") as String
                val thenContent = data.getStringExtra("then") as String
                val titleContent = data.getStringExtra("title") as String
                val colorPstn = data.getIntExtra("color", 0)
                val isNotificationTrue = data.getBooleanExtra("notificationSwitch", false)
                val yearString = data.getStringExtra("yearString") as String
                val monthString = data.getStringExtra("monthString") as String
                val dateString = data.getStringExtra("dateString") as String
                val dayStringRaw = data.getStringExtra("dayStringRaw") as String
                val pMRaw = data.getStringExtra("pMRaw") as String
                val hourString = data.getStringExtra("hourString") as String
                val minString = data.getStringExtra("minString") as String
                val madeAt = data.getStringExtra("madeAt") as String

                titleList.add(titleContent)
                ifList.add(ifContent)
                thenList.add(thenContent)
                colorList.add(colorPstn)
                notificationBooleanList.add(isNotificationTrue)
                yearList.add(yearString)
                monthList.add(monthString)
                dateList.add(dateString)
                dayList.add(dayStringRaw)
                pMList.add(pMRaw)
                hourList.add(hourString)
                minList.add(minString)
                madeAtList.add(madeAt)

                addLater = Plan(
                    titleContent,
                    ifContent,
                    thenContent,
                    colorPstn,
                    isNotificationTrue,
                    yearString,
                    monthString,
                    dateString,
                    dayStringRaw,
                    pMRaw,
                    hourString,
                    minString,
                    madeAt
                )
            }
        } else if (requestCode == DETAIL_PLAN) {

            if (resultCode == DELETE) {
                val pstn = data!!.getIntExtra("position", 0)

                deleteLater = madeAtList[pstn]
                removeAtPosition(pstn)

            } else if (resultCode == EDIT) {
                val pstn = data!!.getIntExtra("position", 0)
                val titleContent = data.getStringExtra("title")
                val ifContent = data.getStringExtra("if")
                val thenContent = data.getStringExtra("then")
                val colorContent = data.getIntExtra("color", 0)
                val notificationContent = data.getBooleanExtra("notification", false)
                val yearContent = data.getStringExtra("year")
                val monthContent = data.getStringExtra("month")
                val dateContent = data.getStringExtra("date")
                val dayContent = data.getStringExtra("day")
                val pMContent = data.getStringExtra("PM")
                val hourContent = data.getStringExtra("hour")
                val minContent = data.getStringExtra("min")
                val madeAtContent = data.getStringExtra("madeAt")

                changeLaterTITLE = titleContent
                changeLaterIF = ifContent
                changeLaterTHEN = thenContent
                changeLaterCOLOR = colorContent
                changeLaterNOTIF = notificationContent
                changeLaterYEAR = yearContent
                changeLaterMONTH = monthContent
                changeLaterDATE = dateContent
                changeLaterDAY = dayContent
                changeLaterPM = pMContent
                changeLaterHOUR = hourContent
                changeLaterMIN = minContent
                changeLaterMAL = madeAtContent

                titleList[pstn] = titleContent!!
                ifList[pstn] = ifContent!!
                thenList[pstn] = thenContent!!
                colorList[pstn] = colorContent
                notificationBooleanList[pstn] = notificationContent
                yearList[pstn] = yearContent!!
                monthList[pstn] = monthContent!!
                dateList[pstn] = dateContent!!
                dayList[pstn] = dayContent!!
                pMList[pstn] = pMContent!!
                hourList[pstn] = hourContent!!
                minList[pstn] = minContent!!
                madeAtList[pstn] = madeAtContent!!
            }
        }

        if (ifList.isEmpty()) {
            recyclerView.visibility = View.GONE
            empty_view.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            empty_view.visibility = View.GONE

            val viewManager = LinearLayoutManager(this)
            val viewAdapter = RecyclerAdapter(
                this,
                titleList,
                ifList,
                thenList,
                colorList
            )

            setAdapterListener(viewAdapter)

            recyclerView.layoutManager = viewManager
            recyclerView.adapter = viewAdapter
        }
    }

    var addLater: Plan? = null
    var deleteLater: String? = null
    var changeLaterTITLE: String? = null
    var changeLaterIF: String? = null
    var changeLaterTHEN: String? = null
    var changeLaterCOLOR: Int? = null
    var changeLaterNOTIF: Boolean? = null
    var changeLaterYEAR: String? = null
    var changeLaterMONTH: String? = null
    var changeLaterDATE: String? = null
    var changeLaterDAY: String? = null
    var changeLaterPM: String? = null
    var changeLaterHOUR: String? = null
    var changeLaterMIN: String? = null
    var changeLaterMAL: String? = null


    //リサイクラービューのリストのリスナを設定する関数
    private fun setAdapterListener(adapter: RecyclerAdapter) {
        adapter.onItemClick = { pos, view ->

            val toDetailIntent = Intent(view.context, PlanDetail::class.java)

            toDetailIntent.putExtra("if", ifList[pos])
            toDetailIntent.putExtra("then", thenList[pos])
            toDetailIntent.putExtra("position", pos)
            toDetailIntent.putExtra("title", titleList[pos])
            toDetailIntent.putExtra("color", colorList[pos])
            toDetailIntent.putExtra("if_it_is_edit", false)
            toDetailIntent.putExtra("notification", notificationBooleanList[pos])
            toDetailIntent.putExtra("year", yearList[pos])
            toDetailIntent.putExtra("month", monthList[pos])
            toDetailIntent.putExtra("date", dateList[pos])
            toDetailIntent.putExtra("day", dayList[pos])
            toDetailIntent.putExtra("PM", pMList[pos])
            toDetailIntent.putExtra("hour", hourList[pos])
            toDetailIntent.putExtra("minute", minList[pos])
            toDetailIntent.putExtra("madeAt", madeAtList[pos])

            startActivityForResult(toDetailIntent, DETAIL_PLAN)
        }

        //長押し
        adapter.onLongItemClick = { pos, view ->
            val pop = PopupMenu(applicationContext, view)
            pop.inflate(R.menu.context_menu)

            pop.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.clear_plan -> {
                        deleteLater = madeAtList[pos]

                        removeAtPosition(pos)
                        onActivityResult(RESTART, RESTART, null)

                    }
                    R.id.edit -> {
                        val toDetailIntent = Intent(view.context, PlanDetail::class.java)

                        toDetailIntent.putExtra("if", ifList[pos])
                        toDetailIntent.putExtra("then", thenList[pos])
                        toDetailIntent.putExtra("position", pos)
                        toDetailIntent.putExtra("title", titleList[pos])
                        toDetailIntent.putExtra("color", colorList[pos])
                        toDetailIntent.putExtra("if_it_is_edit", true)
                        toDetailIntent.putExtra("notification", notificationBooleanList[pos])
                        toDetailIntent.putExtra("year", yearList[pos])
                        toDetailIntent.putExtra("month", monthList[pos])
                        toDetailIntent.putExtra("date", dateList[pos])
                        toDetailIntent.putExtra("day", dayList[pos])
                        toDetailIntent.putExtra("PM", pMList[pos])
                        toDetailIntent.putExtra("hour", hourList[pos])
                        toDetailIntent.putExtra("minute", minList[pos])
                        toDetailIntent.putExtra("madeAt", madeAtList[pos])

                        startActivityForResult(toDetailIntent, DETAIL_PLAN)
                    }
                }
                true
            }
            pop.show()
        }

        //タップ
        adapter.onMenuItemClick = { pos, view ->
            val pop = PopupMenu(applicationContext, view, END)
            pop.inflate(R.menu.context_menu)

            pop.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.clear_plan -> {
                        deleteLater = madeAtList[pos]

                        removeAtPosition(pos)
                        onActivityResult(RESTART, RESTART, null)

                    }
                    R.id.edit -> {
                        val toDetailIntent = Intent(view.context, PlanDetail::class.java)

                        toDetailIntent.putExtra("if", ifList[pos])
                        toDetailIntent.putExtra("then", thenList[pos])
                        toDetailIntent.putExtra("position", pos)
                        toDetailIntent.putExtra("if_it_is_edit", true)
                        toDetailIntent.putExtra("notification", notificationBooleanList[pos])
                        toDetailIntent.putExtra("year", yearList[pos])
                        toDetailIntent.putExtra("month", monthList[pos])
                        toDetailIntent.putExtra("date", dateList[pos])
                        toDetailIntent.putExtra("day", dayList[pos])
                        toDetailIntent.putExtra("PM", pMList[pos])
                        toDetailIntent.putExtra("hour", hourList[pos])
                        toDetailIntent.putExtra("minute", minList[pos])
                        toDetailIntent.putExtra("madeAt", madeAtList[pos])

                        startActivityForResult(toDetailIntent, DETAIL_PLAN)
                    }
                }
                true
            }
            pop.show()

        }
    }

    //同じインデックスのデータを消す関数
    private fun removeAtPosition(pos: Int) {
        titleList.removeAt(pos)
        ifList.removeAt(pos)
        thenList.removeAt(pos)
        colorList.removeAt(pos)
        notificationBooleanList.removeAt(pos)
        yearList.removeAt(pos)
        monthList.removeAt(pos)
        dateList.removeAt(pos)
        dayList.removeAt(pos)
        pMList.removeAt(pos)
        hourList.removeAt(pos)
        minList.removeAt(pos)
        madeAtList.removeAt(pos)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
