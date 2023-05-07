package com.example.telemedicine.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.telemedicine.repo.PatientProfileRepo
import com.example.telemedicine.ui.userspace.patient.ProfileFragment

class PatientProfileViewModel : ViewModel() {

    private var patientProfileRepo :PatientProfileRepo

    init {
        patientProfileRepo = PatientProfileRepo()
    }

    fun get_patient_profile(context: ProfileFragment,userId:String){
        patientProfileRepo.get_patient_profile(context,userId)
    }
}