package com.pvryan.mobilecodingchallenge.galleryViewPager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableList
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.R
import com.pvryan.mobilecodingchallenge.data.models.Image
import com.pvryan.mobilecodingchallenge.extensions.arguments
import com.pvryan.mobilecodingchallenge.extensions.hide
import com.pvryan.mobilecodingchallenge.extensions.show
import com.pvryan.mobilecodingchallenge.extensions.snackLong
import com.pvryan.mobilecodingchallenge.galleryGrid.GalleryViewModel
import com.transitionseverywhere.ChangeText
import com.transitionseverywhere.TransitionManager
import kotlinx.android.synthetic.main.fragment_gallery_view_pager.clTopBar
import kotlinx.android.synthetic.main.fragment_gallery_view_pager.flBottomBar
import kotlinx.android.synthetic.main.fragment_gallery_view_pager.flViewPager
import kotlinx.android.synthetic.main.fragment_gallery_view_pager.ivCloseButton
import kotlinx.android.synthetic.main.fragment_gallery_view_pager.pager
import kotlinx.android.synthetic.main.fragment_gallery_view_pager.tvImageDescription
import kotlinx.android.synthetic.main.fragment_gallery_view_pager.tvLikes
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.NumberFormat
import java.util.Locale

class GalleryViewPagerFragment : Fragment() {

    private val viewModel by sharedViewModel<GalleryViewModel>()

    private val imagesObserver by lazy {

        object : ObservableList.OnListChangedCallback<ObservableList<Image>>() {

            override fun onItemRangeRemoved(p0: ObservableList<Image>?, p1: Int, p2: Int) {}

            override fun onChanged(list: ObservableList<Image>) {
                pager.adapter?.notifyDataSetChanged()
            }

            override fun onItemRangeMoved(list: ObservableList<Image>, p1: Int, p2: Int, p3: Int) {}

            override fun onItemRangeInserted(list: ObservableList<Image>, positionStart: Int, itemCount: Int) {
                pager.adapter?.notifyDataSetChanged()
            }

            override fun onItemRangeChanged(list: ObservableList<Image>, positionStart: Int, itemCount: Int) {
                pager.adapter?.notifyDataSetChanged()
            }
        }
    }

    private val networkAvailableObserver by lazy {
        Observer<Boolean> { if (it == false) viewModel.snackbarMessage.value = Constants.Errors.noNetwork }
    }

    private val fullScreenObserver by lazy {
        Observer<Boolean> {
            TransitionManager.beginDelayedTransition(flViewPager as ViewGroup)
            if (it == true) {
                clTopBar.hide()
                flBottomBar.hide()
            } else {
                clTopBar.show()
                flBottomBar.show()
            }
        }
    }

    private val snackbarMessageObserver by lazy { Observer<Any> { flViewPager.snackLong(it) } }

    private val onPageChangeListener by lazy {

        object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {

                TransitionManager.beginDelayedTransition(
                        flBottomBar as ViewGroup,
                        ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_IN)
                )

                tvLikes.text = NumberFormat.getIntegerInstance(Locale.US).format(viewModel.images[position].likes)

                tvImageDescription.text = viewModel.images[position].description
                        ?: getString(R.string.text_description_placeholder, position)

                arguments { putInt(Constants.Keys.position, pager.currentItem) }
            }
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
        ivCloseButton.setOnClickListener { activity?.finish() }
    }

    override fun onResume() {
        super.onResume()
        setupViewPager()
        viewModel.images.addOnListChangedCallback(imagesObserver)
    }

    override fun onPause() {
        viewModel.images.removeOnListChangedCallback(imagesObserver)
        pager.removeOnPageChangeListener(onPageChangeListener)
        super.onPause()
    }

    private fun setupViewPager() {
        // Initiate view pager
        pager.apply {
            adapter = GalleryViewPagerAdapter(viewModel.images)
            addOnPageChangeListener(onPageChangeListener)
            val item = arguments?.getInt(Constants.Keys.position) ?: Constants.defaultPosition
            setCurrentItem(item, false)
        }
    }

    private fun setupViewModel() {
        viewModel.apply {
            loadImageUrls(Constants.defaultPage)
            networkAvailable.observe(this@GalleryViewPagerFragment, networkAvailableObserver)
            fullScreen.observe(this@GalleryViewPagerFragment, fullScreenObserver)
            snackbarMessage.observe(this@GalleryViewPagerFragment, snackbarMessageObserver)
        }
    }

    companion object {
        fun newInstance(position: Int) = GalleryViewPagerFragment().arguments {
            putInt(Constants.Keys.position, position)
        }
    }
}
