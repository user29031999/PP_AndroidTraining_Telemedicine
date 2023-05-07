package com.example.telemedicine.repo

import android.content.Context
import com.example.telemedicine.AppConstants
import com.example.telemedicine.ui.userspace.patient.ProfileFragment
import com.google.firebase.database.FirebaseDatabase

class PatientProfileRepo {

    private var patient_profile : HashMap<String,String>? = null

    fun get_patient_profile(context: ProfileFragment, userid:String?){
        FirebaseDatabase.getInstance().getReference(AppConstants.patient_coll_name).child(userid!!).get().addOnCompleteListener{
            patient_profile = it.result.value as HashMap<String, String> /* = java.util.HashMap<kotlin.String, kotlin.String> */
            context!!.display_in_view(patient_profile)
        }
    }
}