package com.pvryan.mobilecodingchallenge.extensions

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Fragment.arguments(bundleAction: Bundle.() -> Unit) = apply {
    arguments = (arguments ?: Bundle()).apply(bundleAction)
}

val Fragment.orientation get() = (activity as? AppCompatActivity)?.orientation

fun Fragment.actionBar(func: ActionBar.() -> Unit) =
        (activity as? AppCompatActivity)?.supportActionBar?.apply(func)
