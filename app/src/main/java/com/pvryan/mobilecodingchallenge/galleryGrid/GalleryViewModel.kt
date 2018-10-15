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
package com.pvryan.mobilecodingchallenge.galleryGrid

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.SingleLiveEvent
import com.pvryan.mobilecodingchallenge.data.models.Image
import com.pvryan.mobilecodingchallenge.data.source.ImagesRepository

class GalleryViewModel(
        context: Application,
        private val imagesRepository: ImagesRepository
) : AndroidViewModel(context) {

    val images = ObservableArrayList<Image>()
    val maxImagesAvailable = MutableLiveData<Int>()
    val lastVisiblePosition = MutableLiveData<Int>()
    val viewPagerPosition = MutableLiveData<Int>()
    val fullScreen = MutableLiveData<Boolean>().apply { value = false }
    val snackbarMessage = SingleLiveEvent<Any>()
    val networkAvailable = MutableLiveData<Boolean>()

    fun start() {
        if (images.isEmpty()) loadImageUrls(Constants.defaultPage)
    }

    fun loadImageUrls(page: Int, imagesPerPage: Int = Constants.imagesPerPage) {

        imagesRepository.loadImages(
                page,
                imagesPerPage,
                success = { images, totalAvailable ->
                    imagesRepository.saveImages(images)
                    this@GalleryViewModel.images.addAll(images)
                    maxImagesAvailable.value = totalAvailable
                },
                failure = { t -> snackbarMessage.value = t.localizedMessage ?: Constants.Errors.unknownError }
        )
    }

    fun preloadImages(context: Context, positionStart: Int) {

        for (i in positionStart until images.size) {
            Glide.with(context)
                    .load(Uri.parse(images[i].urls.thumb))
                    .apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    )
                    .preload()
        }
    }
}
