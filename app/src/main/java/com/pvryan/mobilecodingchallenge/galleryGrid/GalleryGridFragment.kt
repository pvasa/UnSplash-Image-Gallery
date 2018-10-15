package com.pvryan.mobilecodingchallenge.galleryGrid

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableList
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.R
import com.pvryan.mobilecodingchallenge.data.models.Image
import com.pvryan.mobilecodingchallenge.extensions.arguments
import com.pvryan.mobilecodingchallenge.extensions.orientation
import com.pvryan.mobilecodingchallenge.extensions.snackLong
import com.pvryan.mobilecodingchallenge.galleryViewPager.GalleryViewPagerActivity
import kotlinx.android.synthetic.main.fragment_gallery_grid.rvImages
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GalleryGridFragment : Fragment() {

    private val viewModel by sharedViewModel<GalleryViewModel>()

    private val imagesCallback by lazy {

        object : ObservableList.OnListChangedCallback<ObservableList<Image>>() {

            override fun onChanged(list: ObservableList<Image>) {
                rvImages.adapter?.notifyDataSetChanged()
            }

            override fun onItemRangeRemoved(list: ObservableList<Image>, positionStart: Int, itemCount: Int) {
                rvImages.adapter?.notifyItemRangeRemoved(positionStart, itemCount)
            }

            override fun onItemRangeMoved(list: ObservableList<Image>, p1: Int, p2: Int, p3: Int) {
            }

            override fun onItemRangeInserted(list: ObservableList<Image>, positionStart: Int, itemCount: Int) {
                context?.let { viewModel.preloadImages(it, positionStart) }
                rvImages.adapter?.notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeChanged(list: ObservableList<Image>, positionStart: Int, itemCount: Int) {
                rvImages.adapter?.notifyItemRangeInserted(positionStart, itemCount)
            }
        }
    }

    private val lastVisiblePositionCallback by lazy {
        Observer<Int> {
            if (it == viewModel.images.size - 1) {
                viewModel.loadImageUrls((viewModel.images.size / Constants.imagesPerPage) + 1)
            }
        }
    }

    private val viewPagerPositionCallback by lazy {
        Observer<Int> { rvImages.scrollToPosition(it ?: Constants.defaultPosition) }
    }

    private val maxImagesAvailableCallback by lazy {
        Observer<Int> { if (viewModel.images.size == it) rvImages.clearOnScrollListeners() }
    }

    private val networkAvailableCallback by lazy {
        Observer<Boolean> {
            if (it == false) {
                rvImages.clearOnScrollListeners()
                viewModel.snackbarMessage.value = Constants.Errors.noNetwork
            } else rvImages.addOnScrollListener(scrollListener)
        }
    }

    private val snackbarMessageObserver by lazy { Observer<Any> { rvImages.snackLong(it) } }

    private val scrollListener by lazy {
        GalleryRVScrollListener { viewModel.lastVisiblePosition.value = it }
    }

    private val imageItemUserActionListener = object : ImageItemUserActionListener {

        override fun onImageClick(position: Int) {
            /*val galleryPagerFragment = GalleryViewPagerFragment.newInstance(args)
            activity?.supportFragmentManager?.beginTransaction()
                    ?.add(R.id.container, galleryPagerFragment, GalleryViewPagerFragment.tag)
                    ?.addToBackStack(GalleryViewPagerFragment.tag)
                    ?.commit()*/
            Intent(context, GalleryViewPagerActivity::class.java)
                    .arguments { putInt(Constants.Keys.position, position) }
                    .also { startActivityForResult(it, Constants.RequestCodes.viewPagerActivity) }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_gallery_grid, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupViewModel()
    }

    override fun onResume() {
        super.onResume()
        rvImages.addOnScrollListener(scrollListener)
    }

    override fun onPause() {
        rvImages.clearOnScrollListeners()
        super.onPause()
    }

    private fun setupViewModel() {

        with(viewModel) {

            images.addOnListChangedCallback(imagesCallback)

            lastVisiblePosition.observe(this@GalleryGridFragment, lastVisiblePositionCallback)
            viewPagerPosition.observe(this@GalleryGridFragment, viewPagerPositionCallback)
            maxImagesAvailable.observe(this@GalleryGridFragment, maxImagesAvailableCallback)
            networkAvailable.observe(this@GalleryGridFragment, networkAvailableCallback)
            snackbarMessage.observe(this@GalleryGridFragment, snackbarMessageObserver)
            start()
        }
    }

    private fun setupRecyclerView() {

        // Set span count according to the screen orientation
        val spanCount = if (orientation == Configuration.ORIENTATION_LANDSCAPE) 6 else 4

        // Initialize recycler view
        rvImages.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, spanCount)
            adapter = GalleryGridAdapter(viewModel.images, imageItemUserActionListener)
            arguments?.let { scrollToPosition(it.getInt(Constants.Keys.position)) }
        }
    }

    companion object {
        val tag: String = GalleryGridFragment::class.java.simpleName
        fun newInstance() = GalleryGridFragment()
    }
}
