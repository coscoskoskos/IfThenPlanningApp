package com.coscos.ifthenplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

class NewPlan : AppCompatActivity() {

    private var ifContent: String = ""
    private var thenContent: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_plan)


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
