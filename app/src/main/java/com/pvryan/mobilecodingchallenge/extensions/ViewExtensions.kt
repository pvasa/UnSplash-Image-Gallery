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
@file:Suppress("unused")

package com.pvryan.mobilecodingchallenge.extensions

import android.animation.*
import android.graphics.Point
import android.graphics.Rect
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_gallery.view.*

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun ImageView.zoomImageFromThumb(ivExpandedImage: ImageView, container: FrameLayout,
                                 animator: Animator?) : Animator? {

    val mShortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
    // If there's an animation in progress, cancel it
    // immediately and proceed with this one.
    animator?.cancel()

    container.rvImages.hide()

    var mCurrentAnimator: Animator?

    // Load the high-resolution "zoomed-in" image.
    ivExpandedImage.setImageDrawable(this.drawable)

    // Calculate the starting and ending bounds for the zoomed-in image.
    // This step involves lots of math. Yay, math.
    val startBounds = Rect()
    val finalBounds = Rect()
    val globalOffset = Point()

    // The start bounds are the global visible rectangle of the thumbnail,
    // and the final bounds are the global visible rectangle of the container
    // view. Also set the container view's offset as the origin for the
    // bounds, since that's the origin for the positioning animation
    // properties (X, Y).
    this.getGlobalVisibleRect(startBounds)
    container.getGlobalVisibleRect(finalBounds, globalOffset)
    startBounds.offset(-globalOffset.x, -globalOffset.y)
    finalBounds.offset(-globalOffset.x, -globalOffset.y)

    // Adjust the start bounds to be the same aspect ratio as the final
    // bounds using the "center crop" technique. This prevents undesirable
    // stretching during the animation. Also calculate the start scaling
    // factor (the end scaling factor is always 1.0).
    val startScale: Float
    if (finalBounds.width().toFloat() / finalBounds.height() > startBounds.width().toFloat() / startBounds.height()) {
        // Extend start bounds horizontally
        startScale = startBounds.height().toFloat() / finalBounds.height()
        val startWidth = startScale * finalBounds.width()
        val deltaWidth = (startWidth - startBounds.width()) / 2
        startBounds.left -= deltaWidth.toInt()
        startBounds.right += deltaWidth.toInt()
    } else {
        // Extend start bounds vertically
        startScale = startBounds.width().toFloat() / finalBounds.width()
        val startHeight = startScale * finalBounds.height()
        val deltaHeight = (startHeight - startBounds.height()) / 2
        startBounds.top -= deltaHeight.toInt()
        startBounds.bottom += deltaHeight.toInt()
    }

    // Hide the thumbnail and show the zoomed-in view. When the animation
    // begins, it will position the zoomed-in view in the place of the
    // thumbnail.
    this.alpha = 0f
    ivExpandedImage.show()

    // Set the pivot point for SCALE_X and SCALE_Y transformations
    // to the top-left corner of the zoomed-in view (the default
    // is the center of the view).
    ivExpandedImage.pivotX = 0f
    ivExpandedImage.pivotY = 0f

    // Construct and run the parallel animation of the four translation and
    // scale properties (X, Y, SCALE_X, and SCALE_Y).
    val set = AnimatorSet()
    set.play(ObjectAnimator.ofFloat(ivExpandedImage, View.X,
            startBounds.left.toFloat(), finalBounds.left.toFloat()))
            .with(ObjectAnimator.ofFloat(ivExpandedImage, View.Y,
                    startBounds.top.toFloat(), finalBounds.top.toFloat()))
            .with(ObjectAnimator.ofFloat(ivExpandedImage, View.SCALE_X,
                    startScale, 1f)).with(ObjectAnimator.ofFloat(ivExpandedImage,
            View.SCALE_Y, startScale, 1f))
    set.duration = mShortAnimationDuration.toLong()
    set.interpolator = DecelerateInterpolator()
    set.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            mCurrentAnimator = null
        }

        override fun onAnimationCancel(animation: Animator) {
            mCurrentAnimator = null
        }
    })
    set.start()
    mCurrentAnimator = set

    // Upon clicking the zoomed-in image, it should zoom back down
    // to the original bounds and show the thumbnail instead of
    // the expanded image.
    ivExpandedImage.setOnClickListener {
        mCurrentAnimator?.cancel()

        container.rvImages.show()

        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.
        val expandedSet = AnimatorSet()
        expandedSet.play(ObjectAnimator
                .ofFloat(ivExpandedImage, View.X, startBounds.left.toFloat()))
                .with(ObjectAnimator
                        .ofFloat(ivExpandedImage, View.Y, startBounds.top.toFloat()))
                .with(ObjectAnimator
                        .ofFloat(ivExpandedImage, View.SCALE_X, startScale))
                .with(ObjectAnimator
                        .ofFloat(ivExpandedImage, View.SCALE_Y, startScale))
        expandedSet.duration = mShortAnimationDuration.toLong()
        expandedSet.interpolator = DecelerateInterpolator()
        expandedSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@zoomImageFromThumb.alpha = 1f
                ivExpandedImage.gone()
                mCurrentAnimator = null
            }

            override fun onAnimationCancel(animation: Animator) {
                this@zoomImageFromThumb.alpha = 1f
                ivExpandedImage.gone()
                mCurrentAnimator = null
            }
        })
        expandedSet.start()
        mCurrentAnimator = expandedSet
    }
    return mCurrentAnimator
}
