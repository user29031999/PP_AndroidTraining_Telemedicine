package com.example.telemedicine.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.telemedicine.model.Doctor
import com.example.telemedicine.model.User
import com.example.telemedicine.repo.DoctorRepo
import com.example.telemedicine.repo.RegistrationRepo

class DoctorInfoViewModel : ViewModel() {


    private var doctorRepo : DoctorRepo


    init {

        doctorRepo = DoctorRepo()

    }


    fun register_doctor(context: Context, doctor: Doctor) {
       doctorRepo .register_doctor(context,doctor)
    }

}