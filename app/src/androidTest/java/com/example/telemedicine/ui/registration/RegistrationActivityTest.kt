package com.example.telemedicine.ui.registration

import android.util.Log
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.ui.utils.AlertValidations
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistrationActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(RegistrationActivity::class.java)
    private lateinit var decorView: View

    @Before
    fun setup() {
        activityRule.scenario.onActivity {
            decorView = it.window.decorView
            IdlingRegistry.getInstance().register(it.idling)
        }
    }

    @After
    fun after() {
        activityRule.scenario.onActivity {
            IdlingRegistry.getInstance().unregister(it.idling)
        }
        val firebaseUser = FirebaseAuth.getInstance().currentUser ?: return
        FirebaseDatabase.getInstance().getReference(AppConstants.usr_coll_name)
            .child(FirebaseAuth.getInstance().currentUser!!.uid).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("RegistrationTest", "Generated test user info is deleted")
                    firebaseUser.delete().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("RegistrationTest", "Generated test user auth is deleted")
                        }
                    }
                }
            }
    }

    @Test
    fun validateEmptyForm() {
        onView(withId(R.id.btn_register)).perform(click())

        onView(withText("Empty Fields !!!!!")).check(
            matches(
                withEffectiveVisibility(
                    Visibility.VISIBLE
                )
            )
        )
    }

    @Test
    fun smallPassword() {
        fillRegistrationForm(
            "user@fake.com",
            "pass",
            "pass",
            "Password length is too small has to be greater than 6 letters",
            false
        )
    }

    @Test
    fun mismatchingPasswords() {
        fillRegistrationForm(
            "user@fake.com",
            "pass123",
            "pass",
            "Passwords do not match !!!",
            false
        )
    }

    @Test
    fun emailFormat() {
        fillRegistrationForm(
            "user",
            "pass123",
            "pass123",
            "The email address is badly formatted.",
            true
        )
    }

    @Test
    fun existingUser() {
        fillRegistrationForm(
            "apolinar.ortiz1999@gmail.com",
            "pass123",
            "pass123",
            "The email address is already in use by another account.",
            true
        )
    }

    @Test
    fun validateNewUserRegister() {
        // Using currentTimeMillis to generate an unique user for each test.
        fillRegistrationForm(
            "user${System.currentTimeMillis()}@test.com",
            "pass123",
            "pass123",
            "User registered Sucessfully",
            true
        )
    }

    private fun fillRegistrationForm(
        mail: String,
        password: String,
        confirmPassword: String,
        message: String,
        isToast: Boolean
    ) {
        onView(withId(R.id.et_email)).perform(
            typeText(mail),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_password)).perform(
            typeText(password),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_confirm_password)).perform(
            typeText(confirmPassword),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.btn_register)).perform(click())
        if (isToast) AlertValidations.isToastDisplayed(
            message,
            decorView
        ) else AlertValidations.isSnackbarDisplayed(
            message
        )
    }
}