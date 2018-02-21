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
package com.pvryan.mobilecodingchallenge.ui.extensions

import android.support.design.widget.Snackbar
import android.view.View

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

// Showing snackbar
private fun View.snack(message: String, length: Int,
                       actionTitle: String?, action: View.OnClickListener?): Snackbar {
    val snackbar = Snackbar.make(this, message, length)
    if (actionTitle != null && action != null)
        snackbar.setAction(actionTitle, action).show()
    snackbar.show()
    return snackbar
}
fun View.snackShort(message: String, actionTitle: String? = null,
                    action: View.OnClickListener? = null) {
    this.snack(message, Snackbar.LENGTH_SHORT, actionTitle, action)
}
fun View.snackLong(message: String, actionTitle: String? = null,
                   action: View.OnClickListener? = null) {
    this.snack(message, Snackbar.LENGTH_LONG, actionTitle, action)
}
fun View.snackIndefinite(message: String, actionTitle: String? = null,
                         action: View.OnClickListener? = null): Snackbar
        = this.snack(message, Snackbar.LENGTH_INDEFINITE, actionTitle, action)
