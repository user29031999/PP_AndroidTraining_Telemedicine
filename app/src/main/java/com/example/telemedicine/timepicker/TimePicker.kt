package com.example.telemedicine.timepicker

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import com.example.telemedicine.R
import java.util.*

class TimePicker(
    context: Context,
    is24HourView: Boolean,
    isSpinnerType: Boolean = false
) {

    private var dialog: TimePickerDialog
    private var callback: Callback? = null

    private val listener = OnTimeSetListener { timePicker, hourOfDay, minute ->
        callback?.onTimeSelected(hourOfDay, minute)
    }

    init {
        val style = if (isSpinnerType) R.style.SpinnerTimePickerDialog else 0

        val cal = Calendar.getInstance()

        dialog = TimePickerDialog(context, style, listener,
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), is24HourView)
    }

    fun showDialog(hourOfDay: Int, minute: Int, callback: Callback?) {
        this.callback = callback
        dialog.updateTime(hourOfDay, minute)
        dialog.show()
    }

    interface Callback {
        fun onTimeSelected(hourOfDay: Int, minute: Int)
    }
}