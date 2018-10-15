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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.R

class GalleryViewPagerActivity : AppCompatActivity() {

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
    }
}
