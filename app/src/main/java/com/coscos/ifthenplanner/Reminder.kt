package com.coscos.ifthenplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_reminder.*
import kotlinx.android.synthetic.main.spinner_list.*

class Reminder : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)


    }

    fun toMain(view: View) {
        val intent = Intent(this@Reminder, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}
