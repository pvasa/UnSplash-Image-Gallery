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

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableArrayList
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.SingleLiveEvent
import com.pvryan.mobilecodingchallenge.data.models.Image
import com.pvryan.mobilecodingchallenge.data.source.ImagesDataSource
import com.pvryan.mobilecodingchallenge.data.source.ImagesRepository

class GalleryViewModel(context: Application) : AndroidViewModel(context) {

    val images = ObservableArrayList<Image>()
    val maxImagesAvailable = MutableLiveData<Int>()
    val lastVisiblePosition = MutableLiveData<Int>()
    val viewPagerPosition = MutableLiveData<Int>()
    val fullScreen = MutableLiveData<Boolean>()
    val snackbarMessage = SingleLiveEvent<Any>()
    val networkAvailable = MutableLiveData<Boolean>()

    @SuppressLint("StaticFieldLeak")
    private val context = context.applicationContext
    private val imagesRepository = ImagesRepository()
    private val loadImagesCallback = object : ImagesDataSource.LoadImagesCallback {
        override fun onImagesLoaded(images: ArrayList<Image>) {
            imagesRepository.saveImages(images)
            this@GalleryViewModel.images.addAll(images)
        }

        override fun onTotalImagesAvailable(total: Int) {
            maxImagesAvailable.value = total
        }

        override fun onFailure(t: Throwable?) {
            snackbarMessage.value = t?.localizedMessage ?: Constants.Errors.unknownError
        }
    }

    fun start() {
        if (images.isEmpty()) loadImageUrls(Constants.defaultPage)
    }

    fun loadImageUrls(page: Int, imagesPerPage: Int = Constants.imagesPerPage) {
        imagesRepository.loadImages(loadImagesCallback, page, imagesPerPage)
    }

    fun preloadImages(positionStart: Int) {
        val options = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        for (i in positionStart until images.size) {
            Glide.with(context)
                    .asBitmap()
                    .apply(options)
                    .load(Uri.parse(images[i].urls.thumb))
                    .preload()
        }
    }
}
