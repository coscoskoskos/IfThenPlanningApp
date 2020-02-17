package com.coscos.ifthenplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_plan_detail.*

class PlanDetail : AppCompatActivity() {

    companion object {
        const val DELETE: Int = 3
        const val EDIT: Int = 5
    }

    private var pstn = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_detail)

        if_text.text = intent.getStringExtra("if")
        then_text.text = intent.getStringExtra("then")

        if (intent.getBooleanExtra("if_it_is_edit", false)) {
            //テキストを非表示に
            if_text.visibility = View.GONE
            then_text.visibility = View.GONE

            //エディットを表示
            if_input.visibility = View.VISIBLE
            then_input.visibility = View.VISIBLE

            if_input_text.setText(if_text.text)
            then_input_text.setText(then_text.text)

            button.text = "保存"
            button.background = getDrawable(R.drawable.button_style_prime)

            button_top_right.visibility = View.INVISIBLE
            button_top_right_prime.visibility = View.VISIBLE
        } else {
            if_input.visibility = View.GONE
            then_input.visibility = View.GONE
            button_top_right_prime.visibility = View.INVISIBLE
        }

        pstn = intent.getIntExtra("position", 0)


    }

    //画面中央のボタン
    fun donePlan(view: View) {
        when(button.text) {
            "完了する" -> {
                val intent = Intent(this@PlanDetail, MainActivity::class.java)

                intent.putExtra("position", pstn)

                setResult(DELETE, intent)

                finish()
            }
            "保存" -> {
                if (if_input_text.text.toString() == if_text.text.toString() && then_input_text.text.toString() == then_text.text.toString() && editCount == 0) {
                    //テキストを表示
                    if_text.visibility = View.VISIBLE
                    then_text.visibility = View.VISIBLE

                    //エディットを非表示
                    if_input.visibility = View.GONE
                    then_input.visibility = View.GONE

                    button_top_right.visibility = View.VISIBLE
                    button_top_right_prime.visibility = View.INVISIBLE

                    button.text = "完了する"
                    button.background = getDrawable(R.drawable.button_style)
                } else {

                    val ifContent = if_input_text.text.toString()
                    val thenContent = then_input_text.text.toString()

                    val intent = Intent(this@PlanDetail, MainActivity::class.java)

                    intent.putExtra("position", pstn)
                    intent.putExtra("if", ifContent)
                    intent.putExtra("then", thenContent)

                    setResult(EDIT, intent)

                    finish()

                }
            }
        }
    }

    //編集の状況を管理する変数
    var editCount : Int = 0
    //右上のボタン
    fun startEdit(view: View) {

        when(button_top_right.visibility) {
            View.VISIBLE -> {

                //テキストを非表示に
                if_text.visibility = View.GONE
                then_text.visibility = View.GONE

                //エディットを表示
                if_input.visibility = View.VISIBLE
                then_input.visibility = View.VISIBLE

                if_input_text.setText(if_text.text)
                then_input_text.setText(then_text.text)

                button.text = "保存"
                button.background = getDrawable(R.drawable.button_style_prime)

                button_top_right.visibility = View.INVISIBLE
                button_top_right_prime.visibility = View.VISIBLE
            }
            View.INVISIBLE -> {
                if (editCount == 0) {
                    if (if_input_text.text.toString() == if_text.text.toString() && then_input_text.text.toString() == then_text.text.toString()) {
                        //テキストを表示
                        if_text.visibility = View.VISIBLE
                        then_text.visibility = View.VISIBLE

                        //エディットを非表示
                        if_input.visibility = View.GONE
                        then_input.visibility = View.GONE

                        button_top_right.visibility = View.VISIBLE
                        button_top_right_prime.visibility = View.INVISIBLE

                        button.text = "完了する"
                        button.background = getDrawable(R.drawable.button_style)
                    } else {
                        //テキストを表示
                        if_text.visibility = View.VISIBLE
                        then_text.visibility = View.VISIBLE

                        //エディットを非表示
                        if_input.visibility = View.GONE
                        then_input.visibility = View.GONE

                        button_top_right.visibility = View.VISIBLE
                        button_top_right_prime.visibility = View.INVISIBLE

                        if_text.text = if_input_text.text
                        then_text.text = then_input_text.text

                        ++editCount
                    }
                } else {
                        //テキストを表示
                        if_text.visibility = View.VISIBLE
                        then_text.visibility = View.VISIBLE

                        //エディットを非表示
                        if_input.visibility = View.GONE
                        then_input.visibility = View.GONE

                        button_top_right.visibility = View.VISIBLE
                        button_top_right_prime.visibility = View.INVISIBLE

                        if_text.text = if_input_text.text
                        then_text.text = then_input_text.text

                        ++editCount
                }
            }
        }

    }

    //左上のボタン
    fun stopAdding(view: View) {
        finish()
    }


}
