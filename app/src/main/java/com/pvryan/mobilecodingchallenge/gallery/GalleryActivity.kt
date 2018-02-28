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

import android.arch.lifecycle.Observer
import android.content.IntentFilter
import android.content.res.Configuration
import android.databinding.Observable
import android.databinding.ObservableList
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.pvryan.mobilecodingchallenge.ConnectivityBroadcastReceiver
import com.pvryan.mobilecodingchallenge.ConnectivityListener
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.R
import com.pvryan.mobilecodingchallenge.data.models.Image
import com.pvryan.mobilecodingchallenge.extensions.*
import kotlinx.android.synthetic.main.activity_gallery.*

// Activity for showing images from Unsplash
class GalleryActivity : AppCompatActivity(), ConnectivityListener {

    private lateinit var viewModel: GalleryViewModel

    private val receiver = ConnectivityBroadcastReceiver(this)
    private val imageItemUserActionListener = object : ImageItemUserActionListener {
        override fun onImageClick(position: Int) {
            pager.setCurrentItem(position, false)
            flExpandedImage.show()
            viewModel.fullScreen.set(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        setSupportActionBar(toolbar)
        setupViewModel()
        setupGallery()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        rvImages.addOnScrollListener(GalleryRVScrollListener({
            viewModel.lastVisiblePosition.set(it)
        }))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
        rvImages.clearOnScrollListeners()
    }

    override fun onConnectivityStatusChanged() {
        if (!isNetworkAvailable()) {
            rvImages.clearOnScrollListeners()
            rvImages.snackIndefinite(Constants.Errors.noNetwork,
                    R.string.text_try_again, View.OnClickListener { setupGallery() })
        }
    }

    private fun setupViewModel() {
        viewModel = obtainViewModel().apply {
            images.addOnListChangedCallback(
                    object : ObservableList.OnListChangedCallback<ObservableList<Image>>() {
                        override fun onChanged(list: ObservableList<Image>) {
                            rvImages.adapter.notifyDataSetChanged()
                            pager.adapter?.notifyDataSetChanged()
                        }
                        override fun onItemRangeRemoved(list: ObservableList<Image>,
                                                        positionStart: Int, itemCount: Int) {
                            rvImages.adapter.notifyItemRangeRemoved(positionStart, itemCount)
                        }
                        override fun onItemRangeMoved(list: ObservableList<Image>,
                                                      p1: Int, p2: Int, p3: Int) {}
                        override fun onItemRangeInserted(
                                list: ObservableList<Image>, positionStart: Int, itemCount: Int) {
                            rvImages.adapter.notifyItemRangeInserted(positionStart, itemCount)
                            pager.adapter?.notifyDataSetChanged()
                        }
                        override fun onItemRangeChanged(list: ObservableList<Image>,
                                                        positionStart: Int, itemCount: Int) {
                            rvImages.adapter.notifyItemRangeInserted(positionStart, itemCount)
                            pager.adapter?.notifyDataSetChanged()
                        }
                    })
            lastVisiblePosition.addOnPropertyChangedCallback(
                    object : Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(p0: Observable?, position: Int) {
                            if (lastVisiblePosition.get() == images.size - 1)
                                loadImages((images.size / Constants.imagesPerPage) + 1)
                        }
                    })
            maxImagesAvailable.addOnPropertyChangedCallback(
                    object : Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(p0: Observable?, maxImagesAvailable: Int) {
                            if (images.size == maxImagesAvailable)
                                rvImages.clearOnScrollListeners()
                        }
                    })
            fullScreen.addOnPropertyChangedCallback(
                    object : Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(p0: Observable?, p1: Int) {
                            if (fullScreen.get()) {
                                appBar.gone()
                                hideStatusBar()
                            } else {
                                showStatusBar()
                                appBar.show()
                            }
                        }
                    })
            snackbarMessage.observe(this@GalleryActivity, Observer {
                rvImages.snackLong(it)
            })
        }
    }

    private fun setupGallery() {
        if (!isNetworkAvailable()) {
            rvImages.snackIndefinite(Constants.Errors.noNetwork,
                    R.string.text_try_again, View.OnClickListener {
                setupGallery()
            })
            return
        }
        setupRecyclerView()
        setupViewPager()
        // If new instance, retrieve new images
        if (viewModel.images.isEmpty()) {
            viewModel.start()
        }
    }

    private fun setupRecyclerView() {
        // Set span count according to the screen orientation
        val spanCount = if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) 4 else 3
        // Initialize recycler view
        rvImages.setHasFixedSize(true)
        rvImages.layoutManager = GridLayoutManager(this, spanCount)
        rvImages.adapter = GalleryAdapter(viewModel.images, imageItemUserActionListener)
    }

    private fun setupViewPager() {
        // Initiate view pager
        pager.adapter = ExpandedImagePagerAdapter(viewModel.images, supportFragmentManager)
        buttonClose.setOnClickListener {
            viewModel.fullScreen.set(false)
            rvImages.scrollToPosition(pager.currentItem)
            flExpandedImage.gone()
        }
    }

    private fun obtainViewModel() = obtainViewModel(GalleryViewModel::class.java)
}
