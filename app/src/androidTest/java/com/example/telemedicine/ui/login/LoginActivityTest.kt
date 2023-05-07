package com.example.telemedicine.ui.login

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.telemedicine.R
import com.example.telemedicine.ui.onboarding.UserChoice
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @get:Rule
    val intentsTestRule = IntentsRule()
    private var decorView: View? = null

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
    fun validateIncorrectForm() {
        onView(withId(R.id.btn_register)).perform(click())
        onView(withText("Empty Fields")).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        submitForm("notanemail", "notapassword")
        onView(withText("Login failed incorrect email and password retry")).check(
            matches(
                withEffectiveVisibility(Visibility.VISIBLE)
            )
        )
    }

    @Test
    fun validateForm() {
        submitForm("apolinar.ortiz1999@gmail.com", "pruebas")
        intending(toPackage(UserChoice::class.java.name)).respondWith(
            Instrumentation.ActivityResult(
                Activity.RESULT_OK,
                null
            )
        )
    }

    private fun submitForm(mail: String, password: String) {
        onView(withId(R.id.et_email)).perform(
            typeText(mail),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_password)).perform(
            typeText(password),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.btn_register)).perform(click())
    }
}