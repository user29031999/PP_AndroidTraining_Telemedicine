package com.example.telemedicine.ui.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.telemedicine.R
import com.example.telemedicine.databinding.ActivityUserChoiceBinding


/*** Saswati Santra ***/

class UserChoice : AppCompatActivity() {

    private lateinit var userChoiceBinding: ActivityUserChoiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userChoiceBinding = DataBindingUtil.setContentView(this@UserChoice,
            R.layout.activity_user_choice)

        userChoiceBinding.cvDoctorUser.setOnClickListener {
            var intent = Intent(this@UserChoice,DoctorInfo::class.java)
            startActivity(intent)
        }

        userChoiceBinding.cvPatientUser.setOnClickListener {
            var intent = Intent(this@UserChoice,PatientInfo::class.java)
            startActivity(intent)
        }
    }
}