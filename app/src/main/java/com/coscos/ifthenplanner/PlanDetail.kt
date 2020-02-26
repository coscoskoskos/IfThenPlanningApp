package com.coscos.ifthenplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_plan_detail.*
import java.text.DateFormat.getDateInstance
import java.util.*

class PlanDetail : AppCompatActivity(), NotificationPick.OnDateSelectedListener,
    TimePick.OnTimeSelectedListener {

    companion object {
        const val DELETE: Int = 3
        const val EDIT: Int = 5
    }

    lateinit var yearString: String
    lateinit var monthString: String
    lateinit var dateString: String
    lateinit var dayStringRaw: String
    lateinit var pMRaw: String
    lateinit var hourString: String
    lateinit var minString: String

    val spinnerItems: Array<String> = arrayOf("ピンク", "レッド", "ブルー", "パープル", "グリーン", "グレー", "ブラック")
    val spinnerColors: Array<String> = arrayOf(
        "tag_pink",
        "tag_red",
        "tag_blue",
        "tag_purple",
        "tag_green",
        "tag_grey",
        "tag_black"
    )

    private var pstn = 0

    private var madeAt = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_detail)

        if_text.text = intent.getStringExtra("if")
        then_text.text = intent.getStringExtra("then")
        title_text.setText(intent.getStringExtra("title"))

        yearString = intent.getStringExtra("year")!!
        monthString = intent.getStringExtra("month")!!
        dateString = intent.getStringExtra("date")!!
        dayStringRaw = intent.getStringExtra("day")!!
        pMRaw = intent.getStringExtra("PM")!!
        val notification = intent.getBooleanExtra("notification", false)
        Log.i("notification check", "Boolean: $notification")
        hourString = intent.getStringExtra("hour")!!
        minString = intent.getStringExtra("minute")!!

        madeAt = intent.getStringExtra("madeAt")!!

        findViewById<TextView>(R.id.date).text = ("${yearString}年${monthString}月${dateString}日（${dayStringRaw}）")
        findViewById<TextView>(R.id.push_time).text = ("${pMRaw}${hourString}時${minString}分")

        if (notification) {
            switch1.isChecked = true
        }

        val spinner = findViewById<Spinner>(R.id.spinner)

        val adapter = SpinnerAdapter(
            this.applicationContext,
            R.layout.spinner_list,
            spinnerItems,
            spinnerColors
        )

        spinner.adapter = adapter

        spinner.setSelection(intent.getIntExtra("color", 0))

        if (intent.getBooleanExtra("if_it_is_edit", false)) {
            //テキストを非表示に
            if_text.visibility = View.GONE
            then_text.visibility = View.GONE

            //エディットを表示
            if_input.visibility = View.VISIBLE
            then_input.visibility = View.VISIBLE

            if_input_text.setText(if_text.text)
            then_input_text.setText(then_text.text)

            button_edit.visibility = View.INVISIBLE

        } else {
            if_input_text.setText(if_text.text)
            then_input_text.setText(then_text.text)
            if_input.visibility = View.GONE
            then_input.visibility = View.GONE
        }

        //あとでMainActivityに返す
        pstn = intent.getIntExtra("position", 0)

    }

    //「完了にする」のボタン
    fun donePlan(view: View) {
        val intent = Intent(this@PlanDetail, MainActivity::class.java)

        intent.putExtra("position", pstn)

        setResult(DELETE, intent)

        finish()
    }

    //「変更を保存」のボタン
    fun savePlan(view: View) {
        val titleContent = title_text.text.toString()
        val ifContent = if_input_text.text.toString()
        val thenContent = then_input_text.text.toString()
        val colorPstn = spinner.selectedItemPosition
        val isNotificationTrue = switch1.isChecked

        val intent = Intent(this@PlanDetail, MainActivity::class.java)

        intent.putExtra("position", pstn)
        intent.putExtra("title", titleContent)
        intent.putExtra("if", ifContent)
        intent.putExtra("then", thenContent)
        intent.putExtra("color", colorPstn)
        intent.putExtra("notification", isNotificationTrue)
        intent.putExtra("year", yearString)
        intent.putExtra("month", monthString)
        intent.putExtra("date", dateString)
        intent.putExtra("day", dayStringRaw)
        intent.putExtra("PM", pMRaw)
        intent.putExtra("hour", hourString)
        intent.putExtra("min", minString)
        intent.putExtra("madeAt", madeAt)

        setResult(EDIT, intent)

        finish()
    }


    //チェックボタン
    fun saveEdit(view: View) {

        when (button_edit.visibility) {
            //編集ボタンが表示中の（->編集中でない）とき
            View.VISIBLE -> {
                //編集を保存
                savePlan(view)
            }

            //編集ボタンが非表示（->編集中）のとき
            View.GONE -> {

                //テキストを表示
                if_text.visibility = View.VISIBLE
                then_text.visibility = View.VISIBLE

                //エディットを非表示
                if_input.visibility = View.GONE
                then_input.visibility = View.GONE

                button_edit.visibility = View.VISIBLE

                if_text.text = if_input_text.text
                then_text.text = then_input_text.text

            }
        }
    }

    //ペンボタン
    fun startEdit(view: View) {
        //テキストを非表示に
        if_text.visibility = View.GONE
        then_text.visibility = View.GONE

        //エディットを表示
        if_input.visibility = View.VISIBLE
        then_input.visibility = View.VISIBLE

        //文字列をセット
        if_input_text.setText(if_text.text)
        then_input_text.setText(then_text.text)

        //ボタンを非表示に
        button_edit.visibility = View.GONE
    }


    fun setNotificationDate(view: View) {
        val newFragment = NotificationPick()
        newFragment.show(supportFragmentManager, "notificationPicker")
    }

    //日付選択のonSelectedインターフェース実装
    override fun onSelected(year: Int, month: Int, day: Int) {
        val cal = Calendar.getInstance()
        cal.set(year, month, day)

        val setDayOfWeek = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "日"
            Calendar.MONDAY -> "月"
            Calendar.TUESDAY -> "火"
            Calendar.WEDNESDAY -> "水"
            Calendar.THURSDAY -> "木"
            Calendar.FRIDAY -> "金"
            Calendar.SATURDAY -> "土"
            else -> "日"
        }

        yearString = year.toString()
        monthString = month.toString()
        dateString = day.toString()
        dayStringRaw = setDayOfWeek

        date.text = ("${year}年${month + 1}月${day}日（${setDayOfWeek}）")
        switch1.isChecked = true
    }

    //時間選択のonSelectedインターフェース実装
    override fun onSelected(hour: Int, minute: Int) {
        var setPmAm: String? = null
        var setHour: String? = null
        var setMinute: String? = null

        if (hour >= 12) {
            setPmAm = "午後"

            setHour = (hour - 12).toString()
        } else {
            setPmAm = "午前"

            setHour = hour.toString()
        }
        setMinute = minute.toString()

        pMRaw = setPmAm
        hourString = setHour
        minString = setMinute


        push_time.text = ("$setPmAm${setHour}時${setMinute}分")

        switch1.isChecked = true
    }


    fun setNotificationTime(view: View) {
        val nextFragment = TimePick()
        nextFragment.show(supportFragmentManager, "timePicker")
    }


    //左上のボタン
    fun stopAdding(view: View) {
        finish()
    }


}
