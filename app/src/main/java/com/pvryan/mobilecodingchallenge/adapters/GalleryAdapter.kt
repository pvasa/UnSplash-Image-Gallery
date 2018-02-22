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

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.R
import com.pvryan.mobilecodingchallenge.data.Image
import com.pvryan.mobilecodingchallenge.ui.ExpandedImageActivity
import com.pvryan.mobilecodingchallenge.ui.GalleryActivity
import com.pvryan.mobilecodingchallenge.ui.LoadImagesListener
import com.pvryan.mobilecodingchallenge.utils.RetrofitHelper
import com.transitionseverywhere.Explode
import com.transitionseverywhere.Transition
import com.transitionseverywhere.TransitionManager
import kotlinx.android.synthetic.main.item_image.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("MemberVisibilityCanBePrivate")
// Adapter for recycler view showing images in grid layout
class GalleryAdapter(private val context: Context, val images: ArrayList<Image> = arrayListOf()) :
        RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    // View holder of each image
    lateinit var viewHolder: GalleryViewHolder

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GalleryViewHolder {

        val imageView = LayoutInflater.from(context)
                .inflate(R.layout.item_image, parent, false)
        viewHolder = GalleryViewHolder(imageView)
        return viewHolder
    }

    // Return total number of images shown
    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {

        // Image at current position
        val image = images[position]

        // Load the image with regular quality
        Glide.with(context)
                .asBitmap()
                .load(Uri.parse(image.urls.regular))
                .into(holder.imageView)

        // Expand the image on click into a new activity
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.imageView.context, ExpandedImageActivity::class.java)
            val args = Bundle()
            // Send all images and current position for view pager to start with
            args.putParcelableArrayList(Constants.Keys.images, images)
            args.putInt(Constants.Keys.position, position)
            intent.putExtras(args)

            val viewRect = Rect()
            it.getGlobalVisibleRect(viewRect)

            // create Explode transition with epicenter
            val explode = Explode()
                    .setEpicenterCallback(object : Transition.EpicenterCallback() {
                        override fun onGetEpicenter(transition: Transition): Rect {
                            return viewRect
                        }
                    })
            explode.duration = it.context.resources.getInteger(
                    android.R.integer.config_shortAnimTime).toLong()

            TransitionManager.beginDelayedTransition(it.parent as ViewGroup, explode)

            with(context as GalleryActivity) {
                this.startActivityForResult(
                    intent, Constants.Codes.expandedImageActivity)
                this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }

        }
    }

    // Remove all items and update recyclerview
    fun removeAllItems() {
        val count = images.size
        images.clear()
        notifyItemRangeRemoved(0, count)
    }

    // Insert items and update recyclerview
    fun insertItems(newImages: ArrayList<Image>) {
        val positionStart = images.size
        images.addAll(newImages)
        notifyItemRangeInserted(positionStart, newImages.size)
    }

    // Hit Unsplash api to retrieve images
    fun loadImages(listener: LoadImagesListener, page: Int = 1) {

        RetrofitHelper.getUnsplashApi().getLatestImages(Constants.appIdUnsplash,
                page = page, perPage = Constants.imagesPerPage)
                .enqueue(object : Callback<List<Image>> {

                    override fun onFailure(call: Call<List<Image>>?, t: Throwable?) {
                        listener.onFailure(t)
                    }

                    override fun onResponse(call: Call<List<Image>>?,
                                            response: Response<List<Image>>) {

                        if (response.body() != null) {
                            val newImages = response.body() as ArrayList<Image>
                            insertItems(newImages)
                        } else onFailure(call, Throwable(Constants.Errors.emptyBody))

                        response.headers()?.let {
                            listener.onTotalImagesAvailable(it.get("X-Total")?.toInt() ?: 0)
                        }
                    }
                })
    }

    // View holder of each image
    class GalleryViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.ivImage as ImageView
    }
}
