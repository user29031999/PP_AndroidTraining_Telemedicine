package com.example.telemedicine.ui.utils

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.CoreMatchers.not


class AlertValidations {
    companion object {
        fun isToastDisplayed(message: String, decorView: View) {
            onView(withText(message))
                .inRoot(withDecorView(not(decorView)))// Here we use decorView
                .check(matches(isDisplayed()))
            /*onView(withText(message)).inRoot(ToastMatcher())
                .check(matches(isDisplayed()))*/
            //onView(withText(message)).inRoot(withContentDescription(message)).check(matches(isDisplayed()))
        }

        fun isSnackbarDisplayed(message: String) {
            Espresso.onView(ViewMatchers.withText(message))
                .check(
                    ViewAssertions.matches(
                        ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
                    )
                )
        }
    }
}