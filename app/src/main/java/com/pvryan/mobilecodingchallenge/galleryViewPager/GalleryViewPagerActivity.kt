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

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.Orientation
import com.pvryan.mobilecodingchallenge.R
import com.pvryan.mobilecodingchallenge.extensions.arguments
import com.pvryan.mobilecodingchallenge.runOnMainThread
import org.koin.androidx.viewmodel.ext.android.viewModel

class GalleryViewPagerActivity : AppCompatActivity() {

    private val viewModel by viewModel<GalleryViewPagerViewModel>()

    private val orientationObserver = Observer<Orientation> {

        requestedOrientation = when (it) {
            Orientation.Portrait -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            Orientation.PortraitInverted -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
            Orientation.Landscape -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            Orientation.LandscapeInverted -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            Orientation.Unknown -> return@Observer
        }
    }

    private val networkAvailableObserver = Observer<Boolean> {
        if (it == false) viewModel.snackbarMessage.value = Constants.Errors.noNetwork
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_gallery_view_pager)

        val position = savedInstanceState?.getInt(Constants.Keys.position)
                ?: intent.extras?.getInt(Constants.Keys.position)
                ?: Constants.defaultPosition

        supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.container, GalleryViewPagerFragment.newInstance(position))
                ?.commit()

        setupViewModel()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) runOnMainThread(300) { viewModel.goFullScreen() }
    }

    override fun onBackPressed() {
        setResult(
                Activity.RESULT_OK,
                Intent().arguments {
                    putInt(Constants.Keys.position, viewModel.pageSelected.value)
                }
        )
        finish()
    }

    private fun setupViewModel() {
        with(viewModel) {
            orientation.observe(this@GalleryViewPagerActivity, orientationObserver)
            networkAvailable.observe(this@GalleryViewPagerActivity, networkAvailableObserver)
        }
    }
}
