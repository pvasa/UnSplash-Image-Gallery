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
package com.pvryan.mobilecodingchallenge

// Constant values for easy access
object Constants {

    // Unsplash api
    const val baseURLUnsplash = "https://api.unsplash.com/"
    const val appIdUnsplash = "433e7e72d2dbd376ff3f8cc4198b08f43d0923ef6134f37d1b170f2f0ff73d3e"
    const val defaultPage = 1
    const val imagesPerPage = 30
    const val defaultPosition = 0

    // Unsplash query params
    object Params {
        const val clientId = "client_id"
        const val page = "page"
        const val perPage = "per_page"
        const val orderBy = "order_by"
    }

    // Keys for saving and passing data
    object Keys {
        const val expandedImage = "expandedImage"
        const val position = "position"
    }

    // Request and response codes
    object Codes {
        const val expandedImageActivity = 1
    }

    object Warnings {
        const val singleObserverNotified =
                "Multiple observers registered but only one will be notified of changes."
    }

    // Error messages
    object Errors {
        const val noNetwork = "Network not available."
        const val invalidImage = "Invalid image. Try again."
        const val unknownError = "Unknown error while fetching images. Try again."
        const val invalidMessageType = "Data-type of message parameter is invalid."
    }
}
