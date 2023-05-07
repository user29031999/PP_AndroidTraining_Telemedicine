package com.example.telemedicine

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

class AppConstants {

    companion object {

        val reports_coll_name: String = "reports"

        val consulation_coll_name: String = "consulations"

        val user_type: String = "P"

        val usr_coll_name: String = "User"

        val doctor_coll_name: String = "Doctor"

        val presciption_coll_name: String = "prescriptions"

        val patient_coll_name: String = "Patient"

        val tag: String = "Fragment"


        val sharedPrefName = "PREFERENCE_NAME"

        val userid = "userid"

        val doctor_info_filled = "doctor_info_filled"

        val patient_info_filled = "patient_info_filled"


        var monthName = arrayOf(
            "Jan", "Feb",
            "Mar", "Apr", "May", "Jun", "July",
            "Aug", "Sep", "Oct", "Nov",
            "Dec"
        )


    }

}