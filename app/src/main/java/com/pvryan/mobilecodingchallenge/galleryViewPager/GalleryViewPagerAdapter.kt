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

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.pvryan.mobilecodingchallenge.data.models.Image
import java.util.ArrayList

// Adapter for view pager in expanded image layout
class GalleryViewPagerAdapter(
        private val images: ArrayList<Image>,
        private val singleTapAction: () -> Unit
) : PagerAdapter() {

    // Return a new ImageView for image at current position
    override fun instantiateItem(container: ViewGroup, position: Int): PhotoView =
            PhotoView(container.context).apply {

                layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )

                container.addView(this)

                val circularProgressDrawable = CircularProgressDrawable(context).apply {
                    setStyle(CircularProgressDrawable.DEFAULT)
                    strokeWidth = 2f
                    centerRadius = 16f
                    start()
                }

                Glide.with(context)
                        .load(Uri.parse(images[position].urls.full))
                        .apply(RequestOptions()
                                .placeholder(circularProgressDrawable)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                        )
                        .into(this)

                setOnViewTapListener { _, _, _ -> singleTapAction() }
            }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as? PhotoView)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object` as? PhotoView

    // Return total number of images
    override fun getCount(): Int = images.size

    // Return page title (publisher's name) for each image/page
    override fun getPageTitle(position: Int): CharSequence? =
            images[position].run { "${user.name} ($width x $height)" }
}
