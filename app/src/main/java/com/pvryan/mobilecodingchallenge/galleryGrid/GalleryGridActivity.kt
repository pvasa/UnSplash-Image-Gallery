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

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pvryan.mobilecodingchallenge.ConnectivityBroadcastReceiver
import com.pvryan.mobilecodingchallenge.ConnectivityListener
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.R
import com.pvryan.mobilecodingchallenge.extensions.isNetworkAvailable
import com.pvryan.mobilecodingchallenge.extensions.snackIndefinite
import kotlinx.android.synthetic.main.activity_gallery_grid.container
import kotlinx.android.synthetic.main.activity_gallery_grid.toolbarGrid
import org.koin.androidx.viewmodel.ext.android.viewModel

// Activity for showing images from Unsplash
class GalleryGridActivity : AppCompatActivity(), ConnectivityListener {

    private val viewModel by viewModel<GalleryGridViewModel>()

    private val receiver = ConnectivityBroadcastReceiver(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_gallery_grid)

        setSupportActionBar(toolbarGrid)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, GalleryGridFragment.newInstance())
                    .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        setupViewModel()
        registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        unregisterReceiver(receiver)
        super.onPause()
    }

    override fun onConnectivityStatusChanged() {
        viewModel.networkAvailable.value = isNetworkAvailable()
    }

    private fun setupViewModel() {

        if (!isNetworkAvailable()) {
            container.snackIndefinite(
                    Constants.Errors.noNetwork,
                    R.string.text_try_again,
                    View.OnClickListener { setupViewModel() }
            )
        }
    }
}
