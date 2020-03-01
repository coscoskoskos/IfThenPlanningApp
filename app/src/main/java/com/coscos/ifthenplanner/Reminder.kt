package com.coscos.ifthenplanner

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.room.Room
import com.coscos.ifthenplanner.Database.AppDatabase
import kotlinx.android.synthetic.main.activity_reminder.*
import kotlinx.android.synthetic.main.spinner_list.*
import kotlinx.coroutines.*


class Reminder : AppCompatActivity() {

    private val job = Job()

    val spinnerColors: Array<String> = arrayOf(
        "tag_pink",
        "tag_red",
        "tag_blue",
        "tag_purple",
        "tag_green",
        "tag_grey",
        "tag_black"
    )
    var backgroundId: MutableList<Int> = mutableListOf()
    var madeAt: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        Log.i("start onCreate:Reminder", "supposed to be first")

        //変数を準備
        val colorInt = intent.getIntExtra("color", 0)
        val titleText = intent.getStringExtra("title")
        val ifText = intent.getStringExtra("if")
        val thenText = intent.getStringExtra("then")
        val monthText = intent.getStringExtra("month")
        val dateText = intent.getStringExtra("date")
        val dayText = intent.getStringExtra("day")
        val pmText = intent.getStringExtra("PM")
        val hourText = intent.getStringExtra("hour")
        val minute = intent.getStringExtra("minute")
        madeAt = intent.getIntExtra("madeAt", 0).toString()

        //カラータグ用のIntのIDを用意
        for (i in spinnerColors.indices) {
            backgroundId.add(
                this.resources.getIdentifier(
                    spinnerColors[i],
                    "drawable",
                    this.packageName
                )
            )
        }

        color_tag.setBackgroundResource(backgroundId[colorInt])
        title_text.text = titleText
        if_text.text = ifText
        then_text.text = thenText
        time_text.text = ("${monthText}月${dateText}日（${dayText}）   $pmText${hourText}時${minute}分")


    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    //バツボタン
    fun finishReminding(view: View) {
        finish()
    }

    inner class doneAsyncTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "planDatabase"
            ).build()
            val dao = db.PlanDao()
            dao.deletePlan(madeAt!!)

            Log.i("asyncState", "id: ${madeAt}, done")

            return null
        }
    }

    //チェックマークFAB
    fun toMain(view: View) {

        doneAsyncTask().execute()
        val intent = Intent(this@Reminder, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("doneMadeAt", madeAt!!)
        Log.i("done chenk", "Reminder doneMadeAt: ${intent.getStringExtra("doneMadeAt")}")
        startActivity(intent)
        finish()

    }


}
