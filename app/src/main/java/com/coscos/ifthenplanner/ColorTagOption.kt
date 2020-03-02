package com.coscos.ifthenplanner

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_color_tag_option.*

class ColorTagOption : AppCompatActivity() {

    lateinit var dataStore: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_tag_option)

        dataStore = getSharedPreferences("DataStore", Context.MODE_PRIVATE)

        val pink = dataStore.getString("pink", "ピンク")
        val red = dataStore.getString("red", "レッド")
        val blue = dataStore.getString("blue", "ブルー")
        val purple = dataStore.getString("purple", "パープル")
        val green = dataStore.getString("green", "グリーン")
        val grey = dataStore.getString("grey", "グレー")
        val black = dataStore.getString("black", "ブラック")

        tag_pink.setText(pink)
        tag_red.setText(red)
        tag_blue.setText(blue)
        tag_purple.setText(purple)
        tag_green.setText(green)
        tag_grey.setText(grey)
        tag_black.setText(black)

        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        } ?: IllegalAccessException("Toolbar cannot be null")
    }

    @SuppressLint("CommitPrefEdits")
    fun doneButton(view: View) {
        val pink = tag_pink.text.toString()
        val red = tag_red.text.toString()
        val blue = tag_blue.text.toString()
        val purple = tag_purple.text.toString()
        val green = tag_green.text.toString()
        val grey = tag_grey.text.toString()
        val black = tag_black.text.toString()

        if (pink == "" || red == "" || blue == "" || purple == "" || green == "" || grey == "" || black == "") {
            val toast = Toast.makeText(this, "空の項目があります。", Toast.LENGTH_SHORT)
            toast.show()
        } else {
            editor = dataStore.edit()
            editor.putString("pink", pink)
            editor.putString("red", red)
            editor.putString("blue", blue)
            editor.putString("purple", purple)
            editor.putString("green", green)
            editor.putString("grey", grey)
            editor.putString("black", black)
            editor.apply()
            val toast = Toast.makeText(this, "変更を保存しました。", Toast.LENGTH_SHORT)
            toast.show()
            finish()
        }

    }
}
