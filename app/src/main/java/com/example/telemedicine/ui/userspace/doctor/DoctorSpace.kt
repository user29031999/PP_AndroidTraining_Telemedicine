package com.example.telemedicine.ui.userspace.doctor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.telemedicine.R
import com.example.telemedicine.databinding.ActivityDoctorSpaceBinding
import com.example.telemedicine.ui.userspace.patient.AppointmentFragment
import com.example.telemedicine.ui.userspace.patient.HomeFragment
import com.example.telemedicine.ui.userspace.patient.ProfileFragment
import com.example.telemedicine.ui.userspace.patient.ReportsFragment
import com.google.android.material.navigation.NavigationBarView

class DoctorSpace : AppCompatActivity() {

    internal lateinit var activityDoctorSpaceBinding: ActivityDoctorSpaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDoctorSpaceBinding = DataBindingUtil.setContentView(this@DoctorSpace,R.layout.activity_doctor_space)

        activityDoctorSpaceBinding.bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
            var fragment: Fragment? = null
            when(it.itemId){

                R.id.nav_appointments -> {
                    fragment = PatientListFragment()
                }

                R.id.nav_profile -> {
                    fragment = DoctorProfileFragment()
                }
            }

            supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,
                R.anim.fade_out,R.anim.slide_in).replace(R.id.root_layout,fragment!!).commit()
            true
        })

        activityDoctorSpaceBinding.bottomNavigation.selectedItemId = R.id.nav_appointments


    }
}