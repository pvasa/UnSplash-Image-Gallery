package com.pvryan.mobilecodingchallenge

import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

inline fun runOnMainThread(delayInMillis: Long = 0, crossinline action: () -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        delay(delayInMillis)
        action()
    }
}
