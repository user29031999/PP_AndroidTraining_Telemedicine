package com.example.telemedicine.repo
import android.util.Log
import com.example.telemedicine.AppConstants
import com.example.telemedicine.ui.userspace.doctor.DoctorProfileFragment
import com.google.firebase.database.FirebaseDatabase

class DoctorProfileRepo {

    private var doctor_profile : HashMap<String,String>?=null


    fun get_doctor_profile(context: DoctorProfileFragment, userid:String?) {
        FirebaseDatabase.getInstance().getReference(AppConstants.doctor_coll_name).
        child(userid!!).get().addOnCompleteListener {
            doctor_profile = it.result.value as HashMap<String, String> /* = java.util.HashMap<kotlin.String, kotlin.String> */
            context!!.display_in_view(doctor_profile)
        }
    }

}