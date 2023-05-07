package com.example.telemedicine.ui.onboarding

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.telemedicine.R
import com.example.telemedicine.databinding.ActivityDoctorInfoBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

/*** Yogiraj Thombre **/

class DoctorInfo : AppCompatActivity() {

    private lateinit var activityDoctorInfoBinding: ActivityDoctorInfoBinding

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var permissionId:Int = 101

    private var degree = listOf("Select Your Degree","MBBS","MD","DNB","FRICS","BAMS","BHMS")

    private var gender = listOf("Select Your Gender","Male","Female")

    private var speciality = listOf("Select Your Speciality","Surgery","Gastroenterology","Neurology","Nephrology","Medicine","Cardiology")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityDoctorInfoBinding = DataBindingUtil.setContentView(this@DoctorInfo,R.layout.activity_doctor_info)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        activityDoctorInfoBinding.btnSave.setOnClickListener {
            check_for_empty_fields()
        }

        activityDoctorInfoBinding.spGender.adapter = ArrayAdapter(this@DoctorInfo,
            R.layout.spinner_item,gender)

        activityDoctorInfoBinding.spDegree.adapter = ArrayAdapter(this@DoctorInfo,
            R.layout.spinner_item,degree)


        activityDoctorInfoBinding.spSpeciality.adapter = ArrayAdapter(this@DoctorInfo,
            R.layout.spinner_item,speciality)

        activityDoctorInfoBinding.tvAutodetectLocation.setOnClickListener {
            getLocation()
        }

        activityDoctorInfoBinding.ivBack.setOnClickListener {
            finish()
        }


    }

    private fun check_for_empty_fields() {
           if (activityDoctorInfoBinding.etName.text.toString().isNotEmpty() &&
               activityDoctorInfoBinding.etPhone.text.toString().isNotEmpty() &&
               activityDoctorInfoBinding.etRegNo.text.toString().isNotEmpty() &&
               !activityDoctorInfoBinding.etAge.text.equals(0) &&
               !activityDoctorInfoBinding.spGender.selectedItem.toString().equals("Select Your Gender") &&
               !activityDoctorInfoBinding.spDegree.selectedItem.toString().equals("Select Your Degree") &&
               !activityDoctorInfoBinding.spSpeciality.selectedItem.toString().equals("Select Your Speciality") &&
               activityDoctorInfoBinding.etExperience.text.toString().isNotEmpty() &&
               activityDoctorInfoBinding.etLocation.text.toString().isNotEmpty()){

               var intent = Intent(this@DoctorInfo,ExtraInfo::class.java)
               intent.putExtra("Name",activityDoctorInfoBinding.etName.text.toString())
               intent.putExtra("Age",activityDoctorInfoBinding.etAge.text.toString())
               intent.putExtra("PhoneNumber",activityDoctorInfoBinding.etPhone.text.toString())
               intent.putExtra("Gender",activityDoctorInfoBinding.spGender.selectedItem.toString())
               intent.putExtra("Degree",activityDoctorInfoBinding.spDegree.selectedItem.toString())
               intent.putExtra("Speciality",activityDoctorInfoBinding.spSpeciality.selectedItem.toString())
               intent.putExtra("Registration number",activityDoctorInfoBinding.etRegNo.text.toString())
               intent.putExtra("Experience", activityDoctorInfoBinding.etExperience.text.toString())
               intent.putExtra("Location", activityDoctorInfoBinding.etLocation.text.toString())

               startActivity(intent)
           }else {
               Toast.makeText(this@DoctorInfo,"Empty Fields",Toast.LENGTH_LONG).show()
           }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(

            (this as Activity?)!!,

            arrayOf(

                Manifest.permission.ACCESS_FINE_LOCATION,

                Manifest.permission.ACCESS_COARSE_LOCATION

            ),

            101

        )
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        activityDoctorInfoBinding.etLocation.setText(list.get(0).locality)
                        activityDoctorInfoBinding.etLocation.isEnabled = false
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
}