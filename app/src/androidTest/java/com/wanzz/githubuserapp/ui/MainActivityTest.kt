package com.wanzz.githubuserapp.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test
import com.wanzz.githubuserapp.R

class MainActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testNavigateToDetailUserActivity() {
        onView(withId(R.id.rv_user)).check(matches(isDisplayed()))

        onView(withId(R.id.rv_user)).perform(click())

        onView(withId(R.id.fab_favorite)).check(matches(isDisplayed()))

        onView(withId(R.id.fab_favorite)).perform(click())

        pressBack()

        onView(withId(R.id.rv_user)).check(matches(isDisplayed()))

        onView(withId(R.id.fab_favorite)).perform(click())

        onView(withId(R.id.rv_user)).check(matches(isDisplayed()))
    }
}