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
package com.pvryan.mobilecodingchallenge.data.source

import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.data.models.Image
import com.pvryan.mobilecodingchallenge.data.source.local.ImagesLocalDataSource
import com.pvryan.mobilecodingchallenge.data.source.remote.ImagesRemoteDataSource
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import retrofit2.HttpException

class ImagesRepository {

    private val local = ImagesLocalDataSource()
    private val remote = ImagesRemoteDataSource()

    fun loadImages(
            page: Int,
            imagesPerPage: Int,
            success: (images: ArrayList<Image>, totalAvailable: Int) -> Unit,
            failure: (t: Throwable) -> Unit
    ) {
        GlobalScope.launch {

            val localResponse = local.loadImages(page, imagesPerPage).await()

            if (localResponse.isSuccessful) {

                val images = localResponse.body() as? ArrayList<Image>

                if (images?.isNotEmpty() == true) {

                    val totalAvailable = localResponse.headers()
                            ?.get(Constants.Headers.xTotal)
                            ?.toIntOrNull() ?: images.size

                    GlobalScope.launch(Dispatchers.Main) { success(images, totalAvailable) }
                    return@launch
                }
            }

            val remoteResponse = remote.loadImages(page, imagesPerPage).await()

            GlobalScope.launch(Dispatchers.Main) {

                if (remoteResponse.isSuccessful) {

                    val images = remoteResponse.body() as? ArrayList<Image>
                            ?: run {
                                failure(Throwable(Constants.Errors.unknownError))
                                return@launch
                            }

                    local.saveImages(images)

                    val totalAvailable = remoteResponse.headers()
                            ?.get(Constants.Headers.xTotal)
                            ?.toIntOrNull() ?: images.size

                    success(images, totalAvailable)

                } else failure(HttpException(remoteResponse))
            }
        }
    }

    fun saveImages(images: ArrayList<Image>) {
        local.saveImages(images)
        try {
            remote.saveImages(images)
        } catch (ignored: IllegalAccessError) { // Ignored intentionally
        }
    }
}
