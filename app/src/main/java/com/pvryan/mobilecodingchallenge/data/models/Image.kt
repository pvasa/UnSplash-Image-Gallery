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

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// Image model for Unsplash images
@Parcelize
data class Image(
        val id: String,
        val created_at: String,
        val updated_at: String,
        val width: Int,
        val height: Int,
        val likes: Int,
        val description: String?,
        val urls: Urls,
        val user: User
) : Parcelable
