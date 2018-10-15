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
package com.pvryan.mobilecodingchallenge.data.source.remote

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.data.models.Image
import com.pvryan.mobilecodingchallenge.data.source.ImagesDataSource
import kotlinx.coroutines.experimental.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ImagesRemoteDataSource : ImagesDataSource {

    private val apis: ImagesDataSource.Apis = Retrofit.Builder()
            .baseUrl(Constants.baseURLUnsplash)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(ImagesDataSource.Apis::class.java)

    @Throws(IllegalAccessError::class)
    override fun saveImages(images: ArrayList<Image>) {
        // Only used for local saves
        throw IllegalAccessError(Constants.Errors.illegalAccessErrorRemoteSaves)
    }

    override fun loadImages(page: Int, imagesPerPage: Int): Deferred<Response<List<Image>>> =
            apis.getUnsplashImages(Constants.appIdUnsplash, page, imagesPerPage)
}
