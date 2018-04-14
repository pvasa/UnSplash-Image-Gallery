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

import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.data.models.Image
import com.pvryan.mobilecodingchallenge.data.source.ImagesDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ImagesRemoteDataSource : ImagesDataSource {

    @Throws(IllegalAccessError::class)
    override fun saveImages(images: ArrayList<Image>) {
        // Only used for local saves
        throw IllegalAccessError(Constants.Errors.illegalAccessErrorRemoteSaves)
    }

    @Throws(IllegalAccessError::class)
    override fun loadImages(): ArrayList<Image> {
        // Only used for local loads
        throw IllegalAccessError(Constants.Errors.illegalAccessErrorLocalLoads)
    }

    override fun loadImages(callback: ImagesDataSource.LoadImagesCallback,
                            page: Int, imagesPerPage: Int) {

        val retrofitCalls = Retrofit.Builder()
                .baseUrl(Constants.baseURLUnsplash)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ImagesDataSource.RetrofitCalls::class.java)
                as ImagesDataSource.RetrofitCalls

        retrofitCalls.getUnsplashImagesCall(Constants.appIdUnsplash, page, imagesPerPage)
                .enqueue(object : Callback<List<Image>> {

            override fun onFailure(call: Call<List<Image>>?, t: Throwable?) {
                callback.onFailure(t)
            }

            override fun onResponse(call: Call<List<Image>>?,
                                    response: Response<List<Image>>) {
                when {
                    response.body() != null ->
                        callback.onImagesLoaded(response.body() as ArrayList<Image>)
                    response.errorBody() != null ->
                        onFailure(call, Throwable(response.errorBody()?.string()))
                    else -> onFailure(call, Throwable(Constants.Errors.unknownError))
                }

                response.headers()?.let {
                    callback.onTotalImagesAvailable(it.get("X-Total")?.toInt() ?: 0)
                }
            }
        })
    }
}
