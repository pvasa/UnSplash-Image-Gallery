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
package com.pvryan.mobilecodingchallenge.adapters

import android.animation.Animator
import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.pvryan.mobilecodingchallenge.R
import com.pvryan.mobilecodingchallenge.extensions.zoomImageFromThumb
import com.pvryan.mobilecodingchallenge.ui.GalleryActivity
import kotlinx.android.synthetic.main.item_image.view.*

class GalleryAdapter(private val context: Context, private val images: ArrayList<String>) :
        RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GalleryViewHolder {
        val imageView = LayoutInflater.from(context)
                .inflate(R.layout.item_image, parent, false)
        return GalleryViewHolder(imageView, context)
    }
    
    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val imageURL = images[position]
        Glide.with(context)
                .asBitmap()
                .load(Uri.parse(imageURL))
                .into(holder.imageView)
    }

    class GalleryViewHolder(itemView: View, context: Context) :
            RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val imageView = itemView.ivImage as ImageView

        private val container: FrameLayout =
                (context as GalleryActivity).findViewById(R.id.container)
        private val ivExpandedImage: ImageView =
                (context as GalleryActivity).findViewById(R.id.ivExpandedImage)

        init {
            itemView.setOnClickListener(this)
        }

        // Hold a reference to the current animator,
        // so that it can be canceled mid-way.
        private var mCurrentAnimator: Animator? = null

        override fun onClick(v: View?) {
            mCurrentAnimator = imageView.zoomImageFromThumb(
                    ivExpandedImage, container, mCurrentAnimator)
        }
    }
}
