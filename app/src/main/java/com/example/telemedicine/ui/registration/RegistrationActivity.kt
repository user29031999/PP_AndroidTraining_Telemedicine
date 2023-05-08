package com.example.telemedicine.ui.registration

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.idling.CountingIdlingResource
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.databinding.ActivityRegistrationBinding
import com.example.telemedicine.model.User
import com.example.telemedicine.ui.login.LoginActivity
import com.example.telemedicine.ui.onboarding.UserChoice
import com.example.telemedicine.viewmodel.RegistrationViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : AppCompatActivity() {
    private lateinit var registrationBinding: ActivityRegistrationBinding
    private lateinit var registrationViewModel: RegistrationViewModel
    val idling: CountingIdlingResource = CountingIdlingResource("RegistrationResource")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registrationBinding = DataBindingUtil.setContentView(
            this@RegistrationActivity,
            R.layout.activity_registration
        )
        registrationViewModel =
            ViewModelProvider(this@RegistrationActivity).get(RegistrationViewModel::class.java)
        registrationBinding.registrationLayout.btnRegister.setOnClickListener {
            checkForEmptyFields()
        }
        registrationBinding.registrationLayout.tvLogin.setOnClickListener {
            val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
            startActivity(intent)
        }
        registrationBinding.registrationLayout.passwordVisible.setOnClickListener {
            if (registrationBinding.registrationLayout.etPassword.transformationMethod != null) {
                registrationBinding.registrationLayout.passwordVisible.setImageResource(R.drawable.ic_baseline_visibility_24)
                registrationBinding.registrationLayout.etPassword.transformationMethod = null
            } else {
                registrationBinding.registrationLayout.passwordVisible.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                registrationBinding.registrationLayout.etPassword.transformationMethod =
                    PasswordTransformationMethod()
            }
        }
        registrationBinding.registrationLayout.confirmPasswordVisible.setOnClickListener {
            if (registrationBinding.registrationLayout.etConfirmPassword.transformationMethod != null) {
                registrationBinding.registrationLayout.confirmPasswordVisible.setImageResource(R.drawable.ic_baseline_visibility_24)
                registrationBinding.registrationLayout.etConfirmPassword.transformationMethod = null
            } else {
                registrationBinding.registrationLayout.confirmPasswordVisible.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                registrationBinding.registrationLayout.etConfirmPassword.transformationMethod =
                    PasswordTransformationMethod()
            }
        }
    }

    private fun checkForEmptyFields() {
        if (registrationBinding.registrationLayout.etEmail.text.isNotEmpty()
            && registrationBinding.registrationLayout.etPassword.text.isNotEmpty()
            && registrationBinding.registrationLayout.etConfirmPassword.text.isNotEmpty()
        ) {
            val password: String = registrationBinding.registrationLayout.etPassword.text.toString()
            val confirmPassword: String =
                registrationBinding.registrationLayout.etConfirmPassword.text.toString()
            if (password.length < 6) {
                createSnackbar("Password length is too small has to be greater than 6 letters")
            }
            if (password != confirmPassword) {
                createSnackbar("Passwords do not match !!!")
            }
            val user = User(
                registrationBinding.registrationLayout.etEmail.text.toString(),
                AppConstants.user_type
            )
            registrationBinding.progressBar2.visibility = View.VISIBLE
            registrationViewModel.registerUser(
                this@RegistrationActivity,
                user,
                password,
                registrationBinding,
                idling
            )
        } else {
            createSnackbar("Empty Fields !!!!!")
        }
    }

    fun displayMsg(s: String) {
        Toast.makeText(this@RegistrationActivity, s, Toast.LENGTH_LONG).show()
    }

    fun jumpNxtScreen() {
        registrationBinding.progressBar2.visibility = View.GONE
        saveToSharedPrefs()
        val intent = Intent(this@RegistrationActivity, UserChoice::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveToSharedPrefs() {
        val sharedPreference =
            getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString(
            AppConstants.userid,
            FirebaseAuth.getInstance().currentUser!!.uid.toString()
        )
        editor.commit()
    }

    private fun createSnackbar(text: String) {
        val snackbar = Snackbar.make(
            registrationBinding.root, text,
            Snackbar.LENGTH_LONG
        )
        snackbar.show()
    }
}