package com.example.telemedicine.ui.userspace.patient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.telemedicine.R
import com.example.telemedicine.databinding.ActivityPatientSpaceBinding
import com.google.android.material.navigation.NavigationBarView

class PatientSpace : AppCompatActivity() {

    internal lateinit var patientSpaceBinding: ActivityPatientSpaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        patientSpaceBinding = DataBindingUtil.setContentView(this@PatientSpace,R.layout.activity_patient_space)

        patientSpaceBinding.bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
            var fragment: Fragment? = null
            when(it.itemId){
                R.id.nav_home -> {
                    fragment = HomeFragment()
                }

                R.id.nav_appointments -> {
                    fragment = AppointmentFragment()
                }

                R.id.nav_prescriptions -> {
                    fragment = PrescriptionsFragment()
                }

                R.id.nav_profile -> {
                    fragment = ProfileFragment()
                }
            }

            supportFragmentManager.beginTransaction().
            setCustomAnimations(R.anim.slide_in,R.anim.fade_out,
                R.anim.fade_out,R.anim.slide_in).replace(R.id.root_layout,fragment!!).commit()
            true
        })

        patientSpaceBinding.bottomNavigation.selectedItemId = R.id.nav_home


    }
}