package com.coscos.ifthenplanner.Notification

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*



class NotificationPick : DialogFragment(), DatePickerDialog.OnDateSetListener {

    interface OnDateSelectedListener{
        fun onSelected(year: Int, month: Int, day: Int)
    }
    //リスナ-作成
    private lateinit var listener: OnDateSelectedListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDateSelectedListener){
            listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this.context as Context,
            this,
            year,
            month,
            day)

        return datePickerDialog
    }


    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {

        listener.onSelected(year, month, dayOfMonth)

    }

}