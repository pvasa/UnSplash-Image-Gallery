package com.pvryan.mobilecodingchallenge.galleryViewPager

import android.arch.lifecycle.Observer
import android.databinding.ObservableList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.R
import com.pvryan.mobilecodingchallenge.data.models.Image
import com.pvryan.mobilecodingchallenge.extensions.hide
import com.pvryan.mobilecodingchallenge.extensions.show
import com.pvryan.mobilecodingchallenge.extensions.snackLong
import com.pvryan.mobilecodingchallenge.galleryGrid.GalleryViewModel
import com.transitionseverywhere.ChangeText
import com.transitionseverywhere.TransitionManager
import kotlinx.android.synthetic.main.fragment_gallery_view_pager.*
import java.text.NumberFormat
import java.util.*

class GalleryViewPagerFragment : Fragment() {

    private lateinit var viewModel: GalleryViewModel

    private val imagesCallback by lazy {
        object : ObservableList.OnListChangedCallback<ObservableList<Image>>() {
            override fun onItemRangeRemoved(p0: ObservableList<Image>?, p1: Int, p2: Int) {}
            override fun onChanged(list: ObservableList<Image>) {
                pager.adapter?.notifyDataSetChanged()
            }

            override fun onItemRangeMoved(list: ObservableList<Image>,
                                          p1: Int, p2: Int, p3: Int) {
            }

            override fun onItemRangeInserted(
                    list: ObservableList<Image>, positionStart: Int, itemCount: Int) {
                pager.adapter?.notifyDataSetChanged()
            }

            override fun onItemRangeChanged(list: ObservableList<Image>,
                                            positionStart: Int, itemCount: Int) {
                pager.adapter?.notifyDataSetChanged()
            }
        }
    }
    private val networkAvailableCallback by lazy {
        Observer<Boolean> {
            if (it == false) {
                viewModel.snackbarMessage.value = Constants.Errors.noNetwork
            }
        }
    }
    private val fullScreenCallback by lazy {
        Observer<Boolean> {
            TransitionManager.beginDelayedTransition(flViewPager as ViewGroup)
            if (it == true) flBottomBar.hide()
            else flBottomBar.show()
        }
    }
    private val snackbarMessageObserver by lazy {
        Observer<Any> { flViewPager.snackLong(it) }
    }
    private val onPageChangeListener by lazy {
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                TransitionManager.beginDelayedTransition(flBottomBar as ViewGroup,
                        ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_IN))
                tvLikes.text = NumberFormat.getIntegerInstance(Locale.US)
                        .format(viewModel.images[position].likes)
                arguments?.putInt(Constants.Keys.position, pager.currentItem)
                activity?.let {
                    (it as AppCompatActivity).supportActionBar?.let {
                        it.title = viewModel.images[position].user.name
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_gallery_view_pager, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
    }

    override fun onResume() {
        super.onResume()
        setupViewPager()
        viewModel.images.addOnListChangedCallback(imagesCallback)
    }

    override fun onPause() {
        super.onPause()
        viewModel.images.removeOnListChangedCallback(imagesCallback)
        pager.removeOnPageChangeListener(onPageChangeListener)
    }

    private fun setupViewPager() {
        // Initiate view pager
        pager.adapter = GalleryViewPagerAdapter(viewModel.images, childFragmentManager)
        pager.addOnPageChangeListener(onPageChangeListener)
        arguments?.let {
            pager.setCurrentItem(it.getInt(Constants.Keys.position,
                    Constants.defaultPosition), false)
        }
    }

    private fun setupViewModel() {
        viewModel = (activity as GalleryViewPagerActivity).obtainViewModel().apply {
            networkAvailable.observe(this@GalleryViewPagerFragment, networkAvailableCallback)
            fullScreen.observe(this@GalleryViewPagerFragment, fullScreenCallback)
            snackbarMessage.observe(this@GalleryViewPagerFragment, snackbarMessageObserver)
        }
    }

    companion object {
        val tag: String = GalleryViewPagerFragment::class.java.simpleName
        fun newInstance(args: Bundle? = null): GalleryViewPagerFragment {
            val galleryPagerFragment = GalleryViewPagerFragment()
            galleryPagerFragment.arguments = args
            return galleryPagerFragment
        }
    }
}
