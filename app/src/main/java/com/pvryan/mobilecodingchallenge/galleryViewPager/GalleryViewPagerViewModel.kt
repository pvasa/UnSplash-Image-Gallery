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
package com.pvryan.mobilecodingchallenge.galleryViewPager

import android.app.Application
import android.content.Context
import android.net.Uri
import android.view.OrientationEventListener
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.AndroidViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.MutableLiveData
import com.pvryan.mobilecodingchallenge.Orientation
import com.pvryan.mobilecodingchallenge.SingleLiveEvent
import com.pvryan.mobilecodingchallenge.data.models.Image
import com.pvryan.mobilecodingchallenge.data.source.ImagesRepository

class GalleryViewPagerViewModel(
        context: Application,
        private val imagesRepository: ImagesRepository
) : AndroidViewModel(context) {

    val images = ObservableArrayList<Image>()
    val pageSelected = MutableLiveData(0)
    val maxImagesAvailable = MutableLiveData(images.size)
    val fullScreen = MutableLiveData(false)
    val snackbarMessage = SingleLiveEvent<Any>()
    val networkAvailable = MutableLiveData(true)
    val orientation = MutableLiveData<Orientation>(Orientation.Unknown)

    private val orientationEventListener = object : OrientationEventListener(context) {
        override fun onOrientationChanged(orientation: Int) {
            this@GalleryViewPagerViewModel.orientation.value = Orientation.parse(orientation)
        }
    }

    fun start() {
        orientationEventListener.run { if (canDetectOrientation()) enable() }
        if (images.isEmpty()) loadImageUrls(Constants.defaultPage)
    }

    private fun loadImageUrls(page: Int) {

        imagesRepository.loadImages(
                page,
                Constants.imagesPerPage,
                success = { images, totalAvailable ->
                    this@GalleryViewPagerViewModel.images.addAll(images)
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

    fun pageSelected(position: Int) {

        pageSelected.value = position

        if (position == images.size - 3) {
            loadImageUrls((images.size / Constants.imagesPerPage) + 1)
        }
    }

    fun disableOrientationEventListener() {
        orientationEventListener.disable()
    }

    fun goFullScreen() {
        fullScreen.value = true
    }

    fun revertFullScreen() {
        fullScreen.value = false
    }
}
