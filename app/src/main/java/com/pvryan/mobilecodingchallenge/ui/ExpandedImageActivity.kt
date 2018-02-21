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
package com.pvryan.mobilecodingchallenge.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.R
import com.pvryan.mobilecodingchallenge.adapters.ExpandedImagePagerAdapter
import kotlinx.android.synthetic.main.activity_expanded_image.*

// Displays view pager for expanded images
class ExpandedImageActivity : AppCompatActivity() {

    // For sending current image position back to the grid view
    private val resultIntent = Intent()
    private val resultArgs = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expanded_image)

        // Initiate view pager
        pager.adapter = ExpandedImagePagerAdapter(supportFragmentManager, intent.extras)

        // Set current position to the image selected from grid
        pager.setCurrentItem(
                intent.extras.getInt(Constants.Keys.position), false)

        // Keep on saving current position on swipe to send back to grid
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                resultArgs.putInt(Constants.Keys.position, position)
                resultIntent.putExtras(resultArgs)
            }
        })

        buttonClose.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        // Send current state to grid activity
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
