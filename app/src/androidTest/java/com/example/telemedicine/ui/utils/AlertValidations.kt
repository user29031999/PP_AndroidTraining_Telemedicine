package com.example.telemedicine.ui.utils

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText


class AlertValidations {
    companion object {
        fun isToastDisplayed(message: String, decorView: View) {
            onView(withText(message))
                .inRoot(ToastMatcher().apply {
                    matches(isDisplayed())
                })
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