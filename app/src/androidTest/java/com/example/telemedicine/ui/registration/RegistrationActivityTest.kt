package com.example.telemedicine.ui.registration

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
import com.example.telemedicine.R
import com.example.telemedicine.ui.utils.AlertValidations
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