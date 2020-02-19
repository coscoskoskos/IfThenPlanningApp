package com.coscos.ifthenplanner

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.Toast
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_new_plan.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class NewPlan : AppCompatActivity() {

    private var ifContent: String = ""
    private var thenContent: String? = ""

    val spinnerItems: Array<String> = arrayOf("ピンク", "レッド", "ブルー", "パープル", "グリーン", "グレー", "ブラック")
    val spinnerColors: Array<String> = arrayOf("tag_pink", "tag_red", "tag_blue", "tag_purple", "tag_green", "tag_grey", "tag_black")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_plan)

        android.R.layout.simple_spinner_item

        val spinner = findViewById<Spinner>(R.id.spinner)

        var adapter = SpinnerAdapter(this.applicationContext, R.layout.spinner_list, spinnerItems, spinnerColors)



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

        val yearString = SimpleDateFormat("yyyy").format(Date())
        val monthString = SimpleDateFormat("M").format(Date())
        val dateString = SimpleDateFormat("d").format(Date())
        val dayStringRaw = SimpleDateFormat("E").format(Date())
        val PMRaw = SimpleDateFormat("a").format(Date())
        val hourString = SimpleDateFormat("K").format(Date())
        val minString = SimpleDateFormat("m").format(Date())

        val dayString = when (dayStringRaw) {
            "Sun" -> "日"
            "Mon" -> "月"
            "Tue" -> "火"
            "Wed" -> "水"
            "Thu" -> "木"
            "Fri" -> "金"
            "Sat" -> "土"

            else -> null
        }

        val PMString = when (PMRaw) {
            "AM" -> "午前"
            "PM" -> "午後"
            else -> null
        }

        date.text = (yearString + "年" + monthString + "月" + dateString + "日" + "（$dayString）")
        push_time.text = ("$PMString${hourString.toInt()+1}時${minString}分")

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

    fun setNotificationTime(view: View) {
        val nextFragment = TimePick()
        nextFragment.show(supportFragmentManager, "timePicker")
    }


    //作成が完了したときの処理
    private fun doneAdding() {
        ifContent = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.if_input_text).getText().toString()
        thenContent = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.then_input_text).getText().toString()

        if(ifContent == "") {
            Toast.makeText(applicationContext, "「もし」を入力してください", Toast.LENGTH_SHORT).show()
        } else if(thenContent == "") {
            Toast.makeText(applicationContext, "「...する」を入力してください", Toast.LENGTH_SHORT).show()
        } else {

            val intent = Intent(this@NewPlan, MainActivity::class.java)
            intent.putExtra("if", ifContent)
            intent.putExtra("then", thenContent)

            setResult(AppCompatActivity.RESULT_OK, intent)

            finish()
        }
    }
}
