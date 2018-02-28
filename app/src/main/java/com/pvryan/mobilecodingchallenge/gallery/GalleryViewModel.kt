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
package com.pvryan.mobilecodingchallenge.gallery

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableInt
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.SingleLiveEvent
import com.pvryan.mobilecodingchallenge.data.models.Image
import com.pvryan.mobilecodingchallenge.data.source.ImagesDataSource
import com.pvryan.mobilecodingchallenge.data.source.ImagesUnsplashDataSource
import java.util.*

class GalleryViewModel(context: Application) : AndroidViewModel(context) {

    private val imagesUnsplashDataSource = ImagesUnsplashDataSource

    val images = ObservableArrayList<Image>()
    val maxImagesAvailable = ObservableInt()
    val lastVisiblePosition = ObservableInt()
    val fullScreen = ObservableBoolean(false)
    val snackbarMessage = SingleLiveEvent<Any>()

    fun start() {
        loadImages(Constants.defaultPage)
    }

    fun loadImages(page: Int, imagesPerPage: Int = Constants.imagesPerPage) {

        imagesUnsplashDataSource.loadImages(object : ImagesDataSource.LoadImagesCallback {

            override fun onImagesLoaded(images: ArrayList<Image>) {
                this@GalleryViewModel.images.addAll(images)
            }
            override fun onTotalImagesAvailable(total: Int) {
                maxImagesAvailable.set(total)
            }
            override fun onFailure(t: Throwable?) {
                snackbarMessage.value = t?.localizedMessage ?: Constants.Errors.unknownError
            }
        }, page, imagesPerPage)
    }
}
