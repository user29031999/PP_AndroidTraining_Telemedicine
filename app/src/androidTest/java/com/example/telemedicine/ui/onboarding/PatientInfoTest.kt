package com.example.telemedicine.ui.onboarding

import android.content.Context
import android.util.Log
import android.view.View
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.ui.utils.AlertValidations
import com.google.firebase.database.FirebaseDatabase
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicReference

@RunWith(AndroidJUnit4::class)
class PatientInfoTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(PatientInfo::class.java)

    // Don't keep references to objects from onActivity, using AtomicReference allow us to
    // get the decorView.
    private var decorView: AtomicReference<View> = AtomicReference()

    @Before
    fun setup() {
        activityRule.scenario.onActivity {
            decorView.set(it.window.decorView)
            IdlingRegistry.getInstance().register(it.idling)

            val sharedPreference = it.applicationContext.getSharedPreferences(
                AppConstants.sharedPrefName,
                Context.MODE_PRIVATE
            )
            val editor = sharedPreference.edit()
            editor.putString(
                AppConstants.userid,
                System.currentTimeMillis().toString()
            )
            editor.commit()
        }
    }

    @After
    fun after() {
        activityRule.scenario.onActivity {
            IdlingRegistry.getInstance().unregister(it.idling)
            val sharedPreference =
                it.applicationContext.getSharedPreferences(
                    AppConstants.sharedPrefName,
                    Context.MODE_PRIVATE
                )
            val userId = sharedPreference.getString(AppConstants.userid, "")
            if (userId != null) {
                FirebaseDatabase.getInstance().getReference(AppConstants.patient_coll_name)
                    .child(userId).removeValue()
                    .addOnCompleteListener {
                        Log.d("PatientInfoTest", "Remove user test info: ${it.isSuccessful}")
                    }
            }
        }
    }

    @Test
    fun emptyForm() {
        onView(withId(R.id.btn_save)).perform(click())
        AlertValidations.isToastDisplayed(
            "Empty Fields",
            decorView.get()
        )
    }

    @Test
    fun justRequiredFields() {
        onView(withId(R.id.et_name)).perform(typeText("Isidro Apolinar"), closeSoftKeyboard())
            .check(
                matches(
                    withText("Isidro Apolinar")
                )
            )
        onView(withId(R.id.et_phone)).perform(typeText("5566163068"), closeSoftKeyboard())
            .check(
                matches(
                    withText("5566163068")
                )
            )
        onView(withId(R.id.et_age)).perform(typeText("24"), closeSoftKeyboard())
            .check(
                matches(
                    withText("24")
                )
            )
        onView(withId(R.id.sp_bloodGroup)).perform(click())
        onData(allOf(instanceOf(String::class.java), `is`("O+"))).perform(click())
        onView(withId(R.id.sp_bloodGroup)).check(matches(withSpinnerText(containsString("O+"))))

        onView(withId(R.id.sp_gender)).perform(click())
        onData(allOf(instanceOf(String::class.java), `is`("Male"))).perform(click())
        onView(withId(R.id.sp_gender)).check(matches(withSpinnerText(containsString("Male"))))

        onView(withId(R.id.btn_save)).perform(click())
        AlertValidations.isToastDisplayed("Patient registered Successfully", decorView.get())

    }
}