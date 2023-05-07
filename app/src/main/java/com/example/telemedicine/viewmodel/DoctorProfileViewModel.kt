package com.example.telemedicine.viewmodel

import androidx.lifecycle.ViewModel
import com.example.telemedicine.repo.DoctorProfileRepo
import com.example.telemedicine.ui.userspace.doctor.DoctorProfileFragment

class DoctorProfileViewModel : ViewModel() {


    private var doctorProfileRepo : DoctorProfileRepo


    init {

        doctorProfileRepo = DoctorProfileRepo()

    }


    fun get_doctor_profile(context: DoctorProfileFragment, userId:String) {
        doctorProfileRepo.get_doctor_profile(context,userId)
    }




}