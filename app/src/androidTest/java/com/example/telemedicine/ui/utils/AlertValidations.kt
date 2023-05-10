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
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()))
        }

        fun isSnackbarDisplayed(message: String) {
            onView(withText(message))
                .check(
                    matches(
                        withEffectiveVisibility(Visibility.VISIBLE)
                    )
                )
        }
    }
}