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
package com.pvryan.mobilecodingchallenge.data.models

import android.os.Parcel
import android.os.Parcelable

@Suppress("MemberVisibilityCanBePrivate")
// Urls model for multi size images
data class Urls(val raw: String,
                val full: String,
                val regular: String,
                val small: String,
                val thumb: String) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(raw)
        parcel.writeString(full)
        parcel.writeString(regular)
        parcel.writeString(small)
        parcel.writeString(thumb)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Urls> {
        override fun createFromParcel(parcel: Parcel): Urls = Urls(parcel)
        override fun newArray(size: Int): Array<Urls?> = arrayOfNulls(size)
    }
}