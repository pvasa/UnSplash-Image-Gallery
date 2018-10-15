package com.pvryan.mobilecodingchallenge.extensions

import android.content.Intent
import android.os.Bundle

fun Intent.arguments(bundleAction: Bundle.() -> Unit) = apply {
    putExtras(Bundle().apply(bundleAction))
}
