package com.coscos.ifthenplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import android.os.AsyncTask
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import com.coscos.ifthenplanner.Database.AppDatabase
import com.coscos.ifthenplanner.Database.Plan
import com.coscos.ifthenplanner.Database.PlanDao
import kotlinx.coroutines.*
import android.view.Gravity.END


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


    //定数を定義
    companion object {
        const val NEW_PLAN: Int = 1
        const val DETAIL_PLAN: Int = 2
        const val DELETE: Int = 3
        const val RESTART: Int = 4
        const val EDIT: Int = 5
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //アクションバーの設定
        setSupportActionBar(findViewById(R.id.toolbar))

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
                }
            }
        }

        CoroutineScope(Dispatchers.Main + job).launch {
            delay(100)
            if (ifList.isEmpty()) {
                recyclerView.visibility = View.GONE
                empty_view.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                empty_view.visibility = View.GONE

                val viewManager = LinearLayoutManager(applicationContext)
                val viewAdapter = RecyclerAdapter(applicationContext, titleList, ifList, thenList, colorList)

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
    inner class myAsyncTask: AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            val dao = startDB()
            if (deleteLater != null) {
                dao.deletePlan(deleteLater.toString())
                deleteLater = null
            }
            if (addLater != null) {
                //追加する処理
                dao.insertUsers(addLater!!)
                addLater = null
            }
            if (changeLaterIF != null) {
                dao.updatePlan(changedLaterIF!!, changedLaterTHEN!!, changeLaterIF!!, changeLaterTHEN!!)
                changeLaterIF = null
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
        return when(item.itemId) {
            R.id.clear_all -> {
                if(ifList.isEmpty()) {
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
                                ifList.clear()
                                thenList.clear()
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

                titleList.add(titleContent)
                ifList.add(ifContent)
                thenList.add(thenContent)
                colorList.add(colorPstn)

                addLater = Plan(titleContent, ifContent, thenContent, colorPstn, isNotificationTrue,
                    yearString, monthString, dateString, dayStringRaw, pMRaw, hourString, minString)
            }
        } else if (requestCode == DETAIL_PLAN) {

            if (resultCode == DELETE) {
                val pstn = data!!.getIntExtra("position", 0)

                deleteLater = ifList[pstn]
                ifList.removeAt(pstn)
                thenList.removeAt(pstn)
            } else if (resultCode == EDIT) {
                val pstn = data!!.getIntExtra("position", 0)
                val ifContent = data.getStringExtra("if")
                val thenContent = data.getStringExtra("then")

                changedLaterIF = ifList[pstn]
                changedLaterTHEN = thenList[pstn]
                changeLaterIF = ifContent
                changeLaterTHEN= thenContent

                ifList[pstn] = ifContent!!
                thenList[pstn] = thenContent!!
            }
        }

        if (ifList.isEmpty()) {
            recyclerView.visibility = View.GONE
            empty_view.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            empty_view.visibility = View.GONE

            val viewManager = LinearLayoutManager(this)
            val viewAdapter = RecyclerAdapter(this, titleList, ifList, thenList, colorList)

            setAdapterListener(viewAdapter)

            recyclerView.layoutManager = viewManager
            recyclerView.adapter = viewAdapter
        }
    }

    var addLater: Plan? = null
    var deleteLater: String? = null
    var changeLaterIF: String? = null
    var changeLaterTHEN: String? = null
    var changedLaterIF: String? = null
    var changedLaterTHEN: String? = null

    //リサイクラービューのリストのリスナを設定する関数
    private fun setAdapterListener(adapter: RecyclerAdapter) {
        adapter.onItemClick = { pos, view ->

            val toDetailIntent = Intent(view.context, PlanDetail::class.java)

            toDetailIntent.putExtra("if", ifList[pos])
            toDetailIntent.putExtra("then", thenList[pos])
            toDetailIntent.putExtra("position", pos)
            toDetailIntent.putExtra("if_it_is_edit", false)

            startActivityForResult(toDetailIntent, DETAIL_PLAN)
        }

        adapter.onLongItemClick = { pos, view ->
            val pop = PopupMenu(applicationContext, view)
            pop.inflate(R.menu.context_menu)

            pop.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.clear_plan -> {
                        deleteLater = ifList[pos]

                        ifList.removeAt(pos)
                        thenList.removeAt(pos)
                        onActivityResult(RESTART, RESTART, null)

                    }
                    R.id.edit -> {
                        val toDetailIntent = Intent(view.context, PlanDetail::class.java)

                        toDetailIntent.putExtra("if", ifList[pos])
                        toDetailIntent.putExtra("then", thenList[pos])
                        toDetailIntent.putExtra("position", pos)
                        toDetailIntent.putExtra("if_it_is_edit", true)

                        startActivityForResult(toDetailIntent, DETAIL_PLAN)
                    }
                }
                true
            }
            pop.show()
        }

        adapter.onMenuItemClick = { pos, view ->
            val pop = PopupMenu(applicationContext, view, END)
            pop.inflate(R.menu.context_menu)

            pop.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.clear_plan -> {
                        deleteLater = ifList[pos]

                        ifList.removeAt(pos)
                        thenList.removeAt(pos)
                        onActivityResult(RESTART, RESTART, null)

                    }
                    R.id.edit -> {
                        val toDetailIntent = Intent(view.context, PlanDetail::class.java)

                        toDetailIntent.putExtra("if", ifList[pos])
                        toDetailIntent.putExtra("then", thenList[pos])
                        toDetailIntent.putExtra("position", pos)
                        toDetailIntent.putExtra("if_it_is_edit", true)

                        startActivityForResult(toDetailIntent, DETAIL_PLAN)
                    }
                }
                true
            }
            pop.show()

        }
    }

}
