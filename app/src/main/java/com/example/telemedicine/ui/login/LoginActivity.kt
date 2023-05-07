package com.example.telemedicine.ui.login


/*** Kuncappu Kumar ***/

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.test.espresso.idling.CountingIdlingResource
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.databinding.ActivityLoginBinding
import com.example.telemedicine.ui.onboarding.UserChoice
import com.example.telemedicine.ui.userspace.doctor.DoctorSpace
import com.example.telemedicine.ui.userspace.patient.PatientSpace
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {
    private lateinit var bind: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    val idling: CountingIdlingResource = CountingIdlingResource("LoginResource")
    var email: String = ""
    var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)

        firebaseAuth = FirebaseAuth.getInstance()

        bind.loginLayout.btnRegister.setOnClickListener {
            bind.progressBar6.visibility = View.VISIBLE
            email = bind.loginLayout.etEmail.text.toString()
            password = bind.loginLayout.etPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                idling.increment()
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userid = firebaseAuth.currentUser!!.uid
                        Log.d("userid", userid)
                        saveToSharedPrefs()
                        runBlocking { findUserType(userid) }
                        idling.decrement()
                    } else {
                        bind.progressBar6.visibility = View.GONE
                        createSnackbar("Login failed incorrect email and password retry")
                        idling.decrement()
                    }
                }
            } else {
                bind.progressBar6.visibility = View.GONE
                createSnackbar("Empty Fields")
            }
        }

        bind.loginLayout.passwordVisible.setOnClickListener {
            if (bind.loginLayout.etPassword.transformationMethod != null) {
                bind.loginLayout.passwordVisible.setImageResource(R.drawable.ic_baseline_visibility_24)
                bind.loginLayout.etPassword.transformationMethod = null
            } else {
                bind.loginLayout.passwordVisible.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                bind.loginLayout.etPassword.transformationMethod = PasswordTransformationMethod()
            }
        }

    }

    private suspend fun findUserType(userid: String) = coroutineScope {
        Log.d(TAG, "findUserType block")
        FirebaseDatabase.getInstance().getReference(AppConstants.usr_coll_name).child(userid)
            .get().addOnCompleteListener {
                Log.d(
                    "FirebaseAdditionalTask",
                    "OnComplete called: ${it.isSuccessful} ${it.result.value}"
                )
                if (it.isSuccessful && it.result.value != null) {
                    val userInfo = it.result.value as HashMap<*, *>
                    when (userInfo["user_type"]) {
                        "P" -> {
                            runBlocking { checkUserInfo(userid, AppConstants.patient_coll_name) }
                        }

                        "D" -> {
                            runBlocking { checkUserInfo(userid, AppConstants.doctor_coll_name) }
                        }
                    }
                }
            }
    }

    private suspend fun checkUserInfo(userid: String, CollName: String) = coroutineScope {
        val op = false
        Log.d(TAG, "checkUserInfo block")
        FirebaseDatabase.getInstance().getReference(CollName).child(userid).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val userInfo = it.result.value
                    if (userInfo != null) {
                        navigateToUserSpace(CollName)
                    } else {
                        fillRemainingInfo()
                    }
                }
            }
    }


    private fun fillRemainingInfo() {
        bind.progressBar6.visibility = View.GONE
        Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT).show()
        val intent = Intent(
            this@LoginActivity,
            UserChoice::class.java
        )
        startActivity(intent)
        finish()
    }

    private fun navigateToUserSpace(CollName: String) {
        when (CollName) {
            AppConstants.patient_coll_name -> {
                saveToSharedPrefsPatient()
                bind.progressBar6.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT).show()
                val intent = Intent(
                    this@LoginActivity,
                    PatientSpace::class.java
                )
                startActivity(intent)
                finish()
            }

            AppConstants.doctor_coll_name -> {
                saveToSharedPrefsDoc()
                bind.progressBar6.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT).show()
                val intent = Intent(
                    this@LoginActivity,
                    DoctorSpace::class.java
                )
                startActivity(intent)
                finish()
            }
        }
    }

    private fun saveToSharedPrefs() {
        val sharedPreference =
            getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString(
            AppConstants.userid,
            FirebaseAuth.getInstance().currentUser!!.uid
        )
        editor.commit()
    }

    private fun saveToSharedPrefsDoc() {
        val sharedPreference =
            getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putBoolean(AppConstants.doctor_info_filled, true)
        editor.commit()
    }

    private fun saveToSharedPrefsPatient() {
        val sharedPreference =
            getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putBoolean(AppConstants.patient_info_filled, true)
        editor.commit()
    }

    private fun createSnackbar(text: String) {
        val snackbar = Snackbar.make(
            bind.root, text,
            Snackbar.LENGTH_LONG
        )
        snackbar.show()
    }
}