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
package com.pvryan.mobilecodingchallenge.data

import android.os.Parcel
import android.os.Parcelable

@Suppress("MemberVisibilityCanBePrivate")
// Image model for Unsplash images
data class Image(val id: String,
                 val description: String?,
                 val urls: Urls,
                 val user: User) : Parcelable {

    constructor(parcel: Parcel) :
            this(parcel.readString(),
                    parcel.readString(),
                    parcel.readParcelable<Urls>(Urls::class.java.classLoader),
                    parcel.readParcelable<User>(User::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(description)
        parcel.writeParcelable(urls, Parcelable.PARCELABLE_WRITE_RETURN_VALUE)
        parcel.writeParcelable(user, Parcelable.PARCELABLE_WRITE_RETURN_VALUE)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel): Image {
            return Image(parcel)
        }

        override fun newArray(size: Int): Array<Image?> {
            return arrayOfNulls(size)
        }
    }
}
