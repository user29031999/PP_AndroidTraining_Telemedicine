package com.example.telemedicine.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.databinding.ActivitySplashBinding
import com.example.telemedicine.ui.onboarding.UserChoice
import com.example.telemedicine.ui.registration.RegistrationActivity
import com.example.telemedicine.ui.userspace.doctor.DoctorSpace
import com.example.telemedicine.ui.userspace.patient.PatientSpace


/*** Mukesh Jha **/

class SplashActivity : AppCompatActivity() {

    private lateinit var splashBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        val sharedPreference =  getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)

        val userid = sharedPreference.getString(AppConstants.userid,null)

        val doctor_info_filled = sharedPreference.getBoolean(AppConstants.doctor_info_filled,false)

        val patient_info_filled = sharedPreference.getBoolean(AppConstants.patient_info_filled,false)


        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity

            if (doctor_info_filled){
                var intent = Intent(this@SplashActivity, DoctorSpace::class.java)
                startActivity(intent)
                finish()
            }else if (patient_info_filled){
               var intent = Intent(this@SplashActivity, PatientSpace::class.java)
               startActivity(intent)
               finish()
            }else if (!userid.isNullOrEmpty()){
                var intent = Intent(this@SplashActivity, UserChoice::class.java)
                startActivity(intent)
                finish()
            }else {
                startActivity(Intent(this,RegistrationActivity::class.java))
                finish()
            }


        }, 2000)
        }
    }
