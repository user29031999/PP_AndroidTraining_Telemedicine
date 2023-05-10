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
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.test.espresso.idling.CountingIdlingResource
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.databinding.ActivityPatientInfoBinding
import com.example.telemedicine.ui.model.Patient
import com.example.telemedicine.ui.userspace.patient.PatientSpace
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.database.FirebaseDatabase
import java.util.*


/*** Chandan Patro ***/

class PatientInfo : AppCompatActivity() {
    private lateinit var activityPatientInfoBinding: ActivityPatientInfoBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var bloodGroup =
        listOf("Select Your Blood type", "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")
    private var gender = listOf("Select Your Gender", "Male", "Female")
    private var permissionId: Int = 101
    val idling: CountingIdlingResource = CountingIdlingResource("PatientInfoIdling")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPatientInfoBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_patient_info)

        activityPatientInfoBinding.spBloodGroup.adapter = ArrayAdapter(
            this@PatientInfo,
            R.layout.spinner_item, bloodGroup
        )

        activityPatientInfoBinding.spGender.adapter =
            ArrayAdapter(this@PatientInfo, R.layout.spinner_item, gender)

        activityPatientInfoBinding.btnSave.setOnClickListener {

            activityPatientInfoBinding.progressBar7.visibility = View.VISIBLE


            if (activityPatientInfoBinding.etName.text.toString().isNotEmpty() &&
                activityPatientInfoBinding.etPhone.text.toString().isNotEmpty() &&
                !activityPatientInfoBinding.etAge.text.equals(0) &&
                activityPatientInfoBinding.spBloodGroup.selectedItem.toString() != "Select Your Blood type" &&
                activityPatientInfoBinding.spGender.selectedItem.toString() != "Select Your Gender"
            ) {
                val user = Patient(
                    name = activityPatientInfoBinding.etName.text.toString(),
                    age = activityPatientInfoBinding.etAge.text.toString(),
                    phone = activityPatientInfoBinding.etPhone.text.toString(),
                    blood_group = activityPatientInfoBinding.spBloodGroup.selectedItem.toString(),
                    gender = activityPatientInfoBinding.spGender.selectedItem.toString(),
                    med_history = activityPatientInfoBinding.etMedicalHistory.text.toString()

                )
                sendDataToFirebase(user)
            } else {
                Toast.makeText(this@PatientInfo, "Empty Fields", Toast.LENGTH_LONG).show()
            }
        }

        activityPatientInfoBinding.ivBack.setOnClickListener {
            finish()
        }

    }

    private fun sendDataToFirebase(user: Patient) {
        val sharedPreferences =
            getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)
        val firebaseUserId: String? = sharedPreferences.getString(AppConstants.userid, null)

        idling.increment()
        FirebaseDatabase.getInstance().getReference(AppConstants.patient_coll_name)
            .child(firebaseUserId!!).setValue(user).addOnCompleteListener {
                idling.decrement()
                if (it.isSuccessful) {
                    saveToSharedPrefsPatient()
                    activityPatientInfoBinding.progressBar7.visibility = View.GONE
                    val intent = Intent(this@PatientInfo, PatientSpace::class.java)
                    startActivity(intent)
                    finish()
                    Log.d("saving to database", "successful")
                    Toast.makeText(this, "Patient registered Successfully", Toast.LENGTH_LONG)
                        .show()

                } else {
                    Toast.makeText(this, "Failed to register patient retry", Toast.LENGTH_LONG)
                        .show()
                    Log.d("saving to database", "unsuccessful")
                }
            }
    }

    private fun saveToSharedPrefsPatient() {
        val sharedPreference =
            getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putBoolean(AppConstants.patient_info_filled, true)
        editor.commit()
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
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
            ), 101
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
}