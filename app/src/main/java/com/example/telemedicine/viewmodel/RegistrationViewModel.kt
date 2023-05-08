package com.example.telemedicine.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.test.espresso.idling.CountingIdlingResource
import com.example.telemedicine.databinding.ActivityRegistrationBinding
import com.example.telemedicine.model.User
import com.example.telemedicine.repo.RegistrationRepo

class RegistrationViewModel : ViewModel() {


    private var registrationRepo: RegistrationRepo


    init {

        registrationRepo = RegistrationRepo()

    }


    fun register_user(
        context: Context,
        user: User,
        password: String,
        registrationBinding: ActivityRegistrationBinding,
        idling: CountingIdlingResource
    ) {
        registrationRepo.register_user(context, user, password, registrationBinding, idling)
    }

}