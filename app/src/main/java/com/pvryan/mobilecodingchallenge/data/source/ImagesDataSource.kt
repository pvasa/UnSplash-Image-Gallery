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
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ImagesDataSource {

    fun loadImages(): ArrayList<Image>

    fun loadImages(callback: LoadImagesCallback, page: Int = Constants.defaultPage,
                   imagesPerPage: Int = Constants.imagesPerPage)

    fun saveImages(images: ArrayList<Image>)

    interface RetrofitCalls {
        @GET(Constants.EndPoints.photos)
        fun getUnsplashImagesCall(@Query(Constants.Params.clientId) clientId: String,
                                  @Query(Constants.Params.page) page: Int = Constants.defaultPage,
                                  @Query(Constants.Params.perPage) perPage: Int = Constants.imagesPerPage,
                                  @Query(Constants.Params.orderBy) orderBy: String = Constants.defaultOrder)
                : Call<List<Image>>
    }

    interface LoadImagesCallback {
        fun onImagesLoaded(images: ArrayList<Image>)
        fun onTotalImagesAvailable(total: Int)
        fun onFailure(t: Throwable?)
    }
}
