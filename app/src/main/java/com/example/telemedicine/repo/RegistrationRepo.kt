package com.example.telemedicine.repo

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.test.espresso.idling.CountingIdlingResource
import com.example.telemedicine.AppConstants
import com.example.telemedicine.databinding.ActivityRegistrationBinding
import com.example.telemedicine.model.User
import com.example.telemedicine.ui.registration.RegistrationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationRepo {

    private val firebaseAuth = FirebaseAuth.getInstance()


    fun register_user(
        context: Context,
        user: User,
        password: String,
        registrationBinding: ActivityRegistrationBinding,
        idling: CountingIdlingResource
    ) {
        idling.increment()
        firebaseAuth.createUserWithEmailAndPassword(user.email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("registration of user", "sucessful")
                FirebaseDatabase.getInstance().getReference(AppConstants.usr_coll_name)
                    .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(user)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("saving to database", "sucessful")
                            Toast.makeText(
                                context,
                                "User registered Sucessfully",
                                Toast.LENGTH_LONG
                            ).show()
                            move_to_next_screen(context)
                        } else {
                            registrationBinding.progressBar2.visibility = View.GONE
                            Toast.makeText(
                                context,
                                "Failed to register user retry",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("saving to database", "unsuccessful")
                        }
                        idling.decrement()
                    }.addOnCanceledListener {
                        registrationBinding.progressBar2.visibility = View.GONE
                        Toast.makeText(context, "Failed to register user retry", Toast.LENGTH_LONG)
                            .show()
                        Log.d("saving to database", "unsuccessful")
                        idling.decrement()
                    }
            } else {
                registrationBinding.progressBar2.visibility = View.GONE
                Toast.makeText(
                    context,
                    it.exception?.message ?: "User cannot be registered.",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("registration of user", "unsucessful")
                idling.decrement()
            }

        }.addOnCanceledListener {
            registrationBinding.progressBar2.visibility = View.GONE
            Toast.makeText(context, "Failed to register user retry", Toast.LENGTH_LONG).show()
            Log.d("registration of user", "cancelled")
            idling.decrement()
        }
    }

    private fun move_to_next_screen(context: Context) {
        (context as RegistrationActivity).jumpNxtScreen()
    }


}