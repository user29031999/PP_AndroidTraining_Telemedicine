package com.example.telemedicine.ui.onboarding

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.adapter.ChipsAdapter
import com.example.telemedicine.databinding.ActivityExtraInfoBinding
import com.example.telemedicine.model.Doctor
import com.example.telemedicine.timepicker.TimePicker
import com.example.telemedicine.ui.userspace.doctor.DoctorSpace
import com.example.telemedicine.viewmodel.DoctorInfoViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.DateValidatorPointForward.now
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class ExtraInfo : AppCompatActivity() {


    private lateinit var activityExtraInfoBinding: ActivityExtraInfoBinding

    private lateinit var timePicker: TimePicker

    private var date_range_list : MutableList<String> = mutableListOf()

    private var slot_range_list : MutableList<String> = mutableListOf()

    private lateinit var name:String

    private lateinit var age:String

    private lateinit var gender:String

    private lateinit var degree:String

    private lateinit var speciality:String

    private lateinit var experience:String

    private lateinit var location:String

    private lateinit var registration_number:String

    private lateinit var phoneNumber :String

    private lateinit var doctorInfoViewModel: DoctorInfoViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        activityExtraInfoBinding = DataBindingUtil.setContentView(this@ExtraInfo,
            R.layout.activity_extra_info)

        doctorInfoViewModel = ViewModelProvider(this@ExtraInfo).
        get(DoctorInfoViewModel::class.java)


        name = intent.getStringExtra("Name") as String

        age = intent.getStringExtra("Age") as String

        gender = intent.getStringExtra("Gender") as String

        degree = intent.getStringExtra("Degree") as String

        registration_number = intent.getStringExtra("Registration number") as String

        phoneNumber = intent.getStringExtra("PhoneNumber") as String

        speciality = intent.getStringExtra("Speciality") as String

        experience = intent.getStringExtra("Experience") as String

        location = intent.getStringExtra("Location") as String


        timePicker = TimePicker(this, false, true)

        activityExtraInfoBinding.tvSlot1TimeStart.setOnClickListener {
             showTimePickerDialog(activityExtraInfoBinding.tvSlot1TimeStart)
        }

        activityExtraInfoBinding.tvSlot1TimeEnd.setOnClickListener {
            showTimePickerDialog(activityExtraInfoBinding.tvSlot1TimeEnd)
        }

        activityExtraInfoBinding.tvSlot2TimeStart.setOnClickListener {
            showTimePickerDialog(activityExtraInfoBinding.tvSlot2TimeStart)
        }

        activityExtraInfoBinding.tvSlot2TimeEnd.setOnClickListener {
            showTimePickerDialog(activityExtraInfoBinding.tvSlot2TimeEnd)
        }

        activityExtraInfoBinding.tvSlot3TimeStart.setOnClickListener {
            showTimePickerDialog(activityExtraInfoBinding.tvSlot3TimeStart)
        }

        activityExtraInfoBinding.tvSlot3TimeEnd.setOnClickListener {
            showTimePickerDialog(activityExtraInfoBinding.tvSlot3TimeEnd)
        }

        activityExtraInfoBinding.cvSelectDate.setOnClickListener {
            showDatePickerDialog(activityExtraInfoBinding.tvSelDate)
        }

        activityExtraInfoBinding.rvSelDate.adapter = ChipsAdapter(this@ExtraInfo,"add",date_range_list)
        activityExtraInfoBinding.rvSelDate.layoutManager = LinearLayoutManager(this@ExtraInfo,
            LinearLayoutManager.VERTICAL,false)

        activityExtraInfoBinding.btnSave.setOnClickListener {
            validate_all_fields()
        }




    }

    private fun validate_all_fields() {


        var morning_slot_start : String=  activityExtraInfoBinding.tvSlot1TimeStart.text.toString()
        var morning_slot_end : String=  activityExtraInfoBinding.tvSlot1TimeEnd.text.toString()

        var afternoon_slot_start : String= activityExtraInfoBinding.tvSlot2TimeStart.text.toString()
        var afternoon_slot_end : String= activityExtraInfoBinding.tvSlot2TimeEnd.text.toString()

        var evening_slot_start : String= activityExtraInfoBinding.tvSlot3TimeStart.text.toString()
        var evening_slot_end : String= activityExtraInfoBinding.tvSlot3TimeEnd.text.toString()

        if (!morning_slot_start.equals("00:00    am") &&
            !morning_slot_end.equals("00:00    am")) {

            if (!morning_slot_start.contains("pm")) {
                slot_range_list.add("${morning_slot_start}-${morning_slot_end}")
            }else {
                Toast.makeText(this,"Invalid morning time slots",Toast.LENGTH_LONG).show()
                return
            }

        }else {
            Toast.makeText(this,"Invalid morning time slots",Toast.LENGTH_LONG).show()
            return
        }

        if ( !afternoon_slot_start.equals("00:00    am") &&
                    !afternoon_slot_end.equals("00:00    am")){
            if (!afternoon_slot_start.contains("am") && !afternoon_slot_end.contains("am")){
                slot_range_list.add("${afternoon_slot_start}-${afternoon_slot_end}")
            }else {
                Toast.makeText(this,"Invalid afternoon time slots",Toast.LENGTH_LONG).show()
                return
            }
        }else {
            Toast.makeText(this,"Invalid afternoon time slots",Toast.LENGTH_LONG).show()
            return
        }

        if (!evening_slot_start.equals("00:00    am") &&
                    !evening_slot_end.equals("00:00    am")){
            if (!evening_slot_start.contains("am") && !evening_slot_end.contains("am")) {
                slot_range_list.add("${evening_slot_start}-${evening_slot_end}")
            }else {
                Toast.makeText(this,"Invalid evening time slots",Toast.LENGTH_LONG).show()
                return
            }
        }else {
            Toast.makeText(this,"Invalid evening time slots",Toast.LENGTH_LONG).show()
            return
        }

        if (date_range_list.size > 0) {

            var doctor = Doctor(
                name = name,
                age = age,
                gender = gender,
                ph_no = phoneNumber,
                reg_no = registration_number,
                degree = degree,
                speciality = speciality,
                location = location,
                experience = experience,
                date_range = date_range_list,
                slot_range = slot_range_list
            )

            activityExtraInfoBinding.progressBar.visibility = View.VISIBLE
            doctorInfoViewModel.register_doctor(this@ExtraInfo, doctor)
        }else {
            Toast.makeText(this,"Select date range date range is empty",Toast.LENGTH_LONG).show()
        }

    }

    private fun showDatePickerDialog(tvSelDate: TextView) {

        val builder : MaterialDatePicker.Builder<Pair<Long, Long>> = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select Consulation Dates")

        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        calendar.timeInMillis = today
        calendar[Calendar.MONTH] = Calendar.getInstance().get(Calendar.MONTH)
        val decThisYear = calendar.timeInMillis

        val constraintsBuilder : CalendarConstraints =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
                .setEnd(decThisYear).build()

        builder.setCalendarConstraints(constraintsBuilder)

        val picker = builder.build()
        picker.show(supportFragmentManager!!, picker.toString())

        picker.addOnPositiveButtonClickListener {
           var date_range :String = picker.headerText.toString()
            if (!date_range_list.contains(date_range)){
                date_range_list.add(date_range)
                activityExtraInfoBinding.rvSelDate.adapter!!.notifyDataSetChanged()
            }else {
                Toast.makeText(this,"Date Range Already Present",Toast.LENGTH_LONG).show()
            }


        }

    }

    private fun showTimePickerDialog(textView: TextView) {
        val cal = Calendar.getInstance()
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val m = cal.get(Calendar.MINUTE)
        timePicker.showDialog(h, m, object : TimePicker.Callback {
            override fun onTimeSelected(hourOfDay: Int, minute: Int) {
                val hourStr = if (hourOfDay < 10) "0${hourOfDay}" else "${hourOfDay}"
                val minuteStr = if (minute < 10) "0${minute}" else "${minute}"

                if (hourOfDay >= cal.get(Calendar.HOUR_OF_DAY)) {
                    if (hourOfDay == cal.get(Calendar.HOUR_OF_DAY) && minute <= cal.get(Calendar.MINUTE)) {
                        return
                    }
                    //select current after
                } else {
                    //select current before
                }


                val AM_PM: String
                AM_PM = if (hourOfDay < 12) {
                    "am"
                } else {
                    "pm"
                }

                textView.text = "${hourOfDay}:${minuteStr} ${AM_PM}"

            }
        })
    }

    fun jump_to_next_screen() {
        save_to_shared_prefs()
        activityExtraInfoBinding.progressBar.visibility = View.GONE
        var intent = Intent(this@ExtraInfo,DoctorSpace::class.java)
        startActivity(intent)
    }

    private fun save_to_shared_prefs() {
        val sharedPreference =  getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putBoolean(AppConstants.doctor_info_filled,true)
        editor.commit()
    }
}