package com.coscos.ifthenplanner.Notification

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePick: DialogFragment(),
    TimePickerDialog.OnTimeSetListener {

    interface OnTimeSelectedListener{
        fun onSelected(hour: Int, minute: Int)
    }
    private  lateinit var  listener: OnTimeSelectedListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTimeSelectedListener){
            listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        return TimePickerDialog(
            activity,
            this,
            hour,
            minute,
            false
        )
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {

        listener.onSelected(hourOfDay, minute)

    }

}