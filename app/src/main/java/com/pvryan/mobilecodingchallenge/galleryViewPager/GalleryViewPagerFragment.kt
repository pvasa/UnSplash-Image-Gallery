package com.pvryan.mobilecodingchallenge.galleryViewPager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.ObservableList
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.Orientation
import com.pvryan.mobilecodingchallenge.R
import com.pvryan.mobilecodingchallenge.data.models.Image
import com.pvryan.mobilecodingchallenge.extensions.arguments
import com.pvryan.mobilecodingchallenge.extensions.hide
import com.pvryan.mobilecodingchallenge.extensions.hideSystemUi
import com.pvryan.mobilecodingchallenge.extensions.show
import com.pvryan.mobilecodingchallenge.extensions.showSystemUi
import com.pvryan.mobilecodingchallenge.extensions.snackLong
import com.pvryan.mobilecodingchallenge.runOnMainThread
import com.transitionseverywhere.ChangeText
import com.transitionseverywhere.TransitionManager
import kotlinx.android.synthetic.main.fragment_gallery_view_pager.clTopBar
import kotlinx.android.synthetic.main.fragment_gallery_view_pager.flBottomBar
import kotlinx.android.synthetic.main.fragment_gallery_view_pager.flGalleryViewPager
import kotlinx.android.synthetic.main.fragment_gallery_view_pager.ivCloseButton
import kotlinx.android.synthetic.main.fragment_gallery_view_pager.tvImageDescription
import kotlinx.android.synthetic.main.fragment_gallery_view_pager.tvLikes
import kotlinx.android.synthetic.main.fragment_gallery_view_pager.vpGallery
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.NumberFormat
import java.util.Locale

class GalleryViewPagerFragment : Fragment() {

    private val viewModel by sharedViewModel<GalleryViewPagerViewModel>()

    private val imagesObserver = object : ObservableList.OnListChangedCallback<ObservableList<Image>>() {

        override fun onItemRangeRemoved(p0: ObservableList<Image>?, p1: Int, p2: Int) {
            vpGallery.adapter?.notifyDataSetChanged()
        }

        override fun onChanged(list: ObservableList<Image>) {
            vpGallery.adapter?.notifyDataSetChanged()
        }

        override fun onItemRangeMoved(list: ObservableList<Image>, p1: Int, p2: Int, p3: Int) {
            vpGallery.adapter?.notifyDataSetChanged()
        }

        override fun onItemRangeInserted(
                list: ObservableList<Image>,
                positionStart: Int,
                itemCount: Int
        ) {
            vpGallery.adapter?.notifyDataSetChanged()
        }

        override fun onItemRangeChanged(
                list: ObservableList<Image>,
                positionStart: Int,
                itemCount: Int
        ) {
            vpGallery.adapter?.notifyDataSetChanged()
        }
    }

    private val pageSelectedObserver = Observer<Int> { page ->

        TransitionManager.beginDelayedTransition(
                flBottomBar as? ViewGroup,
                ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_IN)
        )

        tvLikes.text = NumberFormat.getIntegerInstance(Locale.US).format(viewModel.images[page].likes)

        tvImageDescription.text = viewModel.images[page].description
                ?: getString(R.string.text_description_placeholder, page)

        arguments { putInt(Constants.Keys.position, vpGallery.currentItem) }
    }

    private val fullScreenObserver = Observer<Boolean> {

        TransitionManager.beginDelayedTransition(flGalleryViewPager as ViewGroup)

        if (it == true) {
            clTopBar.hide()
            flBottomBar.hide()
            runOnMainThread(400) { activity?.hideSystemUi() }
        } else {
            runOnMainThread(400) {
                clTopBar.show()
                flBottomBar.show()
            }
            activity?.showSystemUi()
        }
    }

    private val snackbarMessageObserver = Observer<Any> { flGalleryViewPager.snackLong(it) }

    private val orientationObserver = Observer<Orientation> {

        val navigationBarHeight = resources.getDimensionPixelSize(R.dimen.navigation_bar_height)

        val layoutParams = (flBottomBar.layoutParams as FrameLayout.LayoutParams)

        when (it) {

            Orientation.Portrait,
            Orientation.PortraitInverted -> {
                layoutParams.setMargins(0, 0, 0, navigationBarHeight)
                ivCloseButton.show()
            }

            Orientation.Landscape -> {
                layoutParams.setMargins(0, 0, navigationBarHeight, 0)
                ivCloseButton.show()
            }

            Orientation.LandscapeInverted -> {
                layoutParams.setMargins(navigationBarHeight, 0, 0, 0)
                ivCloseButton.hide()
            }
        }

        flBottomBar.layoutParams = layoutParams
    }

    private val onPageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {

        override fun onPageSelected(position: Int) {
            viewModel.pageSelected(position)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_gallery_view_pager, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupViewPager(savedInstanceState)

        ivCloseButton.setOnClickListener { activity?.onBackPressed() }
    }

    override fun onDestroyView() {
        viewModel.disableOrientationEventListener()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.images.addOnListChangedCallback(imagesObserver)
    }

    override fun onPause() {
        viewModel.images.removeOnListChangedCallback(imagesObserver)
        vpGallery.removeOnPageChangeListener(onPageChangeListener)
        super.onPause()
    }

    private fun setupViewPager(savedInstanceState: Bundle? = null) {

        // Initiate view pager
        vpGallery.apply {

            adapter = GalleryViewPagerAdapter(
                    viewModel.images,
                    singleTapAction = { viewModel.fullScreen.value = !viewModel.fullScreen.value }
            )

            addOnPageChangeListener(onPageChangeListener)

            val position: Int = savedInstanceState?.getInt(Constants.Keys.position)
                    ?: arguments?.getInt(Constants.Keys.position)
                    ?: viewModel.pageSelected.value
                    ?: Constants.defaultPosition

            setCurrentItem(position, false)
        }
    }

    private fun setupViewModel() {
        viewModel.apply {
            images.addOnListChangedCallback(imagesObserver)
            pageSelected.observe(this@GalleryViewPagerFragment, pageSelectedObserver)
            fullScreen.observe(this@GalleryViewPagerFragment, fullScreenObserver)
            snackbarMessage.observe(this@GalleryViewPagerFragment, snackbarMessageObserver)
            orientation.observe(this@GalleryViewPagerFragment, orientationObserver)
            start()
        }
    }

    companion object {

        fun newInstance(
                position: Int
        ) = GalleryViewPagerFragment().arguments { putInt(Constants.Keys.position, position) }
    }
}
