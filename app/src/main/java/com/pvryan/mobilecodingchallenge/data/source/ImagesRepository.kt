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

class ImagesRepository : ImagesDataSource {

    private val local = ImagesLocalDataSource()
    private val remote = ImagesRemoteDataSource()

    @Throws(IllegalAccessError::class)
    override fun loadImages(): ArrayList<Image> {
        // Only used for local loads
        throw IllegalAccessError(Constants.Errors.illegalAccessErrorLocalLoads)
    }

    override fun loadImages(callback: ImagesDataSource.LoadImagesCallback,
                            page: Int, imagesPerPage: Int) {
        if (page == Constants.defaultPage) {
            val images = local.loadImages()
            if (images.isEmpty())
                remote.loadImages(callback)
            else callback.onImagesLoaded(images)
        } else remote.loadImages(callback, page)
    }

    override fun saveImages(images: ArrayList<Image>) {
        local.saveImages(images)
        try {
            remote.saveImages(images)
        } catch (e: IllegalAccessError) { /*Ignored intentionally*/
        }
    }
}
