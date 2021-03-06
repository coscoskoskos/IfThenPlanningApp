package com.coscos.ifthenplanner

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.coscos.ifthenplanner.Adapter.SpinnerAdapter
import com.coscos.ifthenplanner.Notification.NotificationPick
import com.coscos.ifthenplanner.Notification.TimePick
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_plan.*
import java.text.SimpleDateFormat
import java.util.*


class NewPlan : AppCompatActivity(), NotificationPick.OnDateSelectedListener, TimePick.OnTimeSelectedListener {

    private var ifContent: String = ""
    private var thenContent: String = ""
    private var titelContent: String = ""
    private var colorTag: Int = 0
    private var isNotificationTrue: Boolean = false

    val spinnerColors: Array<String> = arrayOf("tag_pink", "tag_red", "tag_blue", "tag_purple", "tag_green", "tag_grey", "tag_black")

    lateinit var yearString: String
    lateinit var monthString: String
    lateinit var dateString: String
    lateinit var dayStringRaw: String
    lateinit var pMRaw: String
    lateinit var hourString: String
    lateinit var minString: String

    lateinit var madeAt: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_plan)

        android.R.layout.simple_spinner_item

        val dataStore = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
        val pink = dataStore.getString("pink", "ピンク")!!
        val red = dataStore.getString("red", "レッド")!!
        val blue = dataStore.getString("blue", "ブルー")!!
        val purple = dataStore.getString("purple", "パープル")!!
        val green = dataStore.getString("green", "グリーン")!!
        val grey = dataStore.getString("grey", "グレー")!!
        val black = dataStore.getString("black", "ブラック")!!

        val spinnerItems: Array<String> = arrayOf(pink, red, blue, purple, green, grey, black)

        val spinner = findViewById<Spinner>(R.id.spinner)

        val adapter = SpinnerAdapter(
            this.applicationContext,
            R.layout.spinner_list,
            spinnerItems,
            spinnerColors
        )

        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            //　アイテムが選択された時
            override fun onItemSelected(
                parent: AdapterView<*>,
                viw: View, position: Int, id: Long
            ) { }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        yearString = SimpleDateFormat("yyyy").format(Date())
        monthString = SimpleDateFormat("M").format(Date())
        dateString = SimpleDateFormat("d").format(Date())
        dayStringRaw = SimpleDateFormat("E").format(Date())
        pMRaw = SimpleDateFormat("a").format(Date())
        val hourText = SimpleDateFormat("K").format(Date()).toInt() + 1
        hourString = hourText.toString()
        minString = SimpleDateFormat("m").format(Date())


        Log.e("nullCheck", "曜日: $dayStringRaw, 午前/午後: $pMRaw")

        val dayString = when (dayStringRaw) {
            "Sun" -> "日"
            "Mon" -> "月"
            "Tue" -> "火"
            "Wed" -> "水"
            "Thu" -> "木"
            "Fri" -> "金"
            "Sat" -> "土"

            else -> dayStringRaw
        }

        val PMString = when (pMRaw) {
            "AM" -> "午前"
            "PM" -> "午後"
            else -> pMRaw
        }

        date.text = ("${yearString}年${monthString}月${dateString}日（$dayString）")
        push_time.text = ("$PMString${hourString}時${minString}分")

    }

    //左上のバツボタン
    fun stopAdding(view: View) {
        finish()
    }

    //右上のチェックボタン
    fun doneCreatingCheck(view: View) {
        doneAdding()
    }

    //中央のDONEボタン
    fun doneButton(view: View) {
        doneAdding()
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
        monthString = (month+1).toString()
        dateString = day.toString()
        dayStringRaw = setDayOfWeek

        date.text = ("${yearString}年${monthString}月${dayStringRaw}日（${setDayOfWeek}）")
        switch1.isChecked = true
    }

    //時間選択のonSelectedインターフェース実装
    override fun onSelected(hour: Int, minute: Int) {
        Log.i("hour", "hour: $hour")

        var setPmAm: String? = null
        var setHour: String? = null
        var setMinute: String? = null

        if (hour >= 12) {
            setPmAm = "午後"

            setHour = (hour-12).toString()
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


    //作成が完了したときの処理
    private fun doneAdding() {
        titelContent = findViewById<TextView>(R.id.title_input_text).getText().toString()
        ifContent = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.if_input_text).getText().toString()
        thenContent = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.then_input_text).getText().toString()
        colorTag = spinner.selectedItemPosition
        isNotificationTrue = switch1.isChecked
        madeAt = SimpleDateFormat("MdKms", Locale.getDefault()).format(Date()).toString()

        if (titelContent == "") {
            Toast.makeText(applicationContext, "タイトルを入力してください", Toast.LENGTH_SHORT).show()
        } else if (ifContent == "") {
            Toast.makeText(applicationContext, "「もし」を入力してください", Toast.LENGTH_SHORT).show()
        } else if(thenContent == "") {
            Toast.makeText(applicationContext, "「...する」を入力してください", Toast.LENGTH_SHORT).show()
        } else {

            val intent = Intent(this@NewPlan, MainActivity::class.java)
            intent.putExtra("title", titelContent)
            intent.putExtra("if", ifContent)
            intent.putExtra("then", thenContent)
            intent.putExtra("color", colorTag)
            intent.putExtra("notificationSwitch", isNotificationTrue)
            intent.putExtra("yearString", yearString)
            intent.putExtra("monthString", monthString)
            intent.putExtra("dateString", dateString)
            intent.putExtra("dayStringRaw", dayStringRaw)
            intent.putExtra("pMRaw", pMRaw)
            intent.putExtra("hourString", hourString)
            intent.putExtra("minString", minString)
            intent.putExtra("madeAt", madeAt)

            setResult(RESULT_OK, intent)

            finish()
        }
    }
}
