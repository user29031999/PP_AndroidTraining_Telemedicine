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
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.databinding.ActivityRegistrationBinding
import com.example.telemedicine.model.Doctor
import com.example.telemedicine.model.User
import com.example.telemedicine.ui.login.LoginActivity
import com.example.telemedicine.ui.onboarding.DoctorInfo
import com.example.telemedicine.ui.onboarding.UserChoice
import com.example.telemedicine.ui.userspace.doctor.DoctorSpace
import com.example.telemedicine.viewmodel.RegistrationViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo

class RegsitrationActivity : AppCompatActivity() {

    private lateinit var registrationBinding: ActivityRegistrationBinding
    private lateinit var registrationViewModel:RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        registrationBinding = DataBindingUtil.setContentView(this@RegsitrationActivity,
            R.layout.activity_registration)

        registrationViewModel = ViewModelProvider(this@RegsitrationActivity).
        get(RegistrationViewModel::class.java)

        registrationBinding.registrationLayout.btnRegister.setOnClickListener {
            check_for_empty_fields()
        }

        registrationBinding.registrationLayout.tvLogin.setOnClickListener {
            var intent = Intent(this@RegsitrationActivity,LoginActivity::class.java)
            startActivity(intent)
        }

        registrationBinding.registrationLayout.passwordVisible.setOnClickListener{
            if (registrationBinding.registrationLayout.etPassword.transformationMethod != null) {
                registrationBinding.registrationLayout.passwordVisible.
                setImageResource(R.drawable.ic_baseline_visibility_24)
                registrationBinding.registrationLayout.etPassword.setTransformationMethod(null)
            }else {
                registrationBinding.registrationLayout.passwordVisible.
                setImageResource(R.drawable.ic_baseline_visibility_off_24)
                registrationBinding.registrationLayout.etPassword.setTransformationMethod(PasswordTransformationMethod())
            }
        }


        registrationBinding.registrationLayout.confirmPasswordVisible.setOnClickListener{
            if (registrationBinding.registrationLayout.etConfirmPassword.transformationMethod != null) {
                registrationBinding.registrationLayout.confirmPasswordVisible.setImageResource(R.drawable.ic_baseline_visibility_24)
                registrationBinding.registrationLayout.etConfirmPassword.setTransformationMethod(null)
            }else {
                registrationBinding.registrationLayout.confirmPasswordVisible.
                setImageResource(R.drawable.ic_baseline_visibility_off_24)
                registrationBinding.registrationLayout.etConfirmPassword.setTransformationMethod(PasswordTransformationMethod())
            }
        }

    }

    private fun check_for_empty_fields(){
        if (registrationBinding.registrationLayout.etEmail.text.isNotEmpty()
            && registrationBinding.registrationLayout.etPassword.text.isNotEmpty()
            && registrationBinding.registrationLayout.etConfirmPassword.text.isNotEmpty()){

            var password : String = registrationBinding.registrationLayout.etPassword.text.toString()
            var confirm_password  : String = registrationBinding.registrationLayout.etConfirmPassword.text.toString()

            if (password.length >= 6) {
                if (password.equals(confirm_password)) {

                    //// do the user registration


                    var user = User(
                        registrationBinding.registrationLayout.etEmail.text.toString(),
                        AppConstants.user_type
                    )


                    registrationBinding.progressBar2.visibility = View.VISIBLE

                    registrationViewModel.register_user(this@RegsitrationActivity,user, password,registrationBinding)

                } else {

                    /// display error msg

                    create_snackbar("Passwords do not match !!!")
                }
            }else {
                create_snackbar("Password length is too small has to be greater than 6 letters")
            }

        }else {

            /// display error msg

            create_snackbar("Empty Fields !!!!!")

        }
    }

    fun display_msg(s:String){
        Toast.makeText(this@RegsitrationActivity,s,Toast.LENGTH_LONG).show()
    }

    fun jump_nxt_screen() {
        registrationBinding.progressBar2.visibility = View.GONE
        save_to_shared_prefs()
        var intent = Intent(this@RegsitrationActivity,UserChoice::class.java)
        startActivity(intent)
        finish()
    }

    private fun save_to_shared_prefs() {
        val sharedPreference =  getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString(AppConstants.userid,FirebaseAuth.getInstance().currentUser!!.uid.toString())
        editor.commit()
    }

    private fun create_snackbar(text:String) {
        val snackbar = Snackbar.make(registrationBinding.root, text,
            Snackbar.LENGTH_LONG)
        snackbar.show()
    }
}