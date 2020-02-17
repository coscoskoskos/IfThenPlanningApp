package com.coscos.ifthenplanner

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment

class DatePick : DialogFragment(), DatePickerDialog.OnDateSetListener {



    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
    }

}