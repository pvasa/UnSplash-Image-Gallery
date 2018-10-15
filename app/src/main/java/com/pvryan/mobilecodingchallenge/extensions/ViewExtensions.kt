/*
 * Copyright 2018 Priyank Vasa
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("unused")

// Extensions functions for View class
package com.pvryan.mobilecodingchallenge.extensions

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.R

// View visibilities
fun View.hide() {
    this.visibility = View.INVISIBLE
}
fun View.gone() {
    this.visibility = View.GONE
}
fun View.show() {
    this.visibility = View.VISIBLE
}

@Throws(IllegalArgumentException::class)
// Showing snackbar
private fun <T> View.snack(message: T, length: Int,
                       actionTitle: Int, action: View.OnClickListener?): Snackbar {
    val snackbar: Snackbar = when (message) {
        is String -> Snackbar.make(this, message, length)
        is Int -> Snackbar.make(this, message, length)
        else -> { throw IllegalArgumentException(Constants.Errors.invalidMessageType)}
    }
    if (action != null)
        snackbar.setAction(actionTitle, action).show()
    snackbar.show()
    return snackbar
}
@Throws(IllegalArgumentException::class)
fun <T> View.snackShort(message: T, actionTitle: Int = R.string.text_dismiss,
                    action: View.OnClickListener? = null) {
    this.snack(message, Snackbar.LENGTH_SHORT, actionTitle, action)
}
@Throws(IllegalArgumentException::class)
fun <T> View.snackLong(message: T, actionTitle: Int = R.string.text_dismiss,
                   action: View.OnClickListener? = null) {
    this.snack(message, Snackbar.LENGTH_LONG, actionTitle, action)
}
@Throws(IllegalArgumentException::class)
fun <T> View.snackIndefinite(message: T, actionTitle: Int = R.string.text_dismiss,
                             action: View.OnClickListener? = null): Snackbar
        = this.snack(message, Snackbar.LENGTH_INDEFINITE, actionTitle, action)
