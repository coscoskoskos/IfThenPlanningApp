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
import android.widget.ArrayAdapter
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T


class NewPlan : AppCompatActivity() {

    private var ifContent: String = ""
    private var thenContent: String? = ""

    val spinnerItems: Array<String> = arrayOf("ピンク", "レッド", "ブルー", "パープル", "グレー", "ブラック")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_plan)

        val spinner = findViewById<Spinner>(R.id.spinner)

        var adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            spinnerItems
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter


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

    fun setNotificationButton(view: View) {
        val newFragment = NotificationPick()
        newFragment.show(supportFragmentManager, "notificationPicker")
    }

    fun setTimeButton(view: View) {
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
