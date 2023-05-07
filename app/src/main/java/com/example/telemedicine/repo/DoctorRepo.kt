package com.example.telemedicine.repo

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.telemedicine.AppConstants
import com.example.telemedicine.model.Doctor
import com.example.telemedicine.ui.onboarding.ExtraInfo
import com.example.telemedicine.ui.registration.RegsitrationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DoctorRepo {


    fun register_doctor(context:Context,doctor: Doctor) {

        val s_p = context.getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)
        val firebase_user_id : String? = s_p.getString(AppConstants.userid,null)

        if (firebase_user_id!!.isNotEmpty()) {

            val hashMap: HashMap<String, String> = HashMap<String, String>() //define empty hashmap
            hashMap.put("user_type", "D")

            FirebaseDatabase.getInstance().getReference(AppConstants.doctor_coll_name)
                .child(firebase_user_id!!).setValue(doctor).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("saving to database", "sucessful")
                    Toast.makeText(context, "Doctor registered Sucessfully", Toast.LENGTH_LONG)
                        .show()
                    move_to_next_screen(context)
                } else {
                    Toast.makeText(context, "Failed to register doctor retry", Toast.LENGTH_LONG)
                        .show()
                    Log.d("saving to database", "unsuccessful")
                }
            }


            FirebaseDatabase.getInstance().getReference(AppConstants.usr_coll_name)
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .updateChildren(hashMap as Map<String, Any>).addOnCompleteListener {

            }
        }
    }

    private fun move_to_next_screen(context: Context) {
        (context as ExtraInfo).jump_to_next_screen()
    }


}