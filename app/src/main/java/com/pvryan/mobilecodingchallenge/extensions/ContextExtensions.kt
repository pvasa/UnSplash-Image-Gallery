package com.pvryan.mobilecodingchallenge.extensions

import android.content.Context

// Return current screen orientation
val Context.orientation: Int get() = resources.configuration.orientation
