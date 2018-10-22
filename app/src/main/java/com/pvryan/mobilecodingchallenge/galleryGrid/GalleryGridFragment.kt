package com.pvryan.mobilecodingchallenge.galleryGrid

import android.app.Activity
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
import androidx.recyclerview.widget.RecyclerView
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

    private val viewModel by sharedViewModel<GalleryGridViewModel>()

    private val imagesObserver = object : ObservableList.OnListChangedCallback<ObservableList<Image>>() {

        override fun onChanged(list: ObservableList<Image>) {
            rvImages.adapter?.notifyDataSetChanged()
        }

        override fun onItemRangeRemoved(
                list: ObservableList<Image>,
                positionStart: Int,
                itemCount: Int
        ) {
            rvImages.adapter?.notifyItemRangeRemoved(positionStart, itemCount)
        }

        override fun onItemRangeMoved(list: ObservableList<Image>, p1: Int, p2: Int, p3: Int) {}

        override fun onItemRangeInserted(
                list: ObservableList<Image>,
                positionStart: Int,
                itemCount: Int
        ) {
            context?.let { viewModel.preloadImages(it, positionStart) }
            rvImages.adapter?.notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeChanged(
                list: ObservableList<Image>,
                positionStart: Int,
                itemCount: Int
        ) {
            rvImages.adapter?.notifyItemRangeInserted(positionStart, itemCount)
        }
    }

    private val maxImagesAvailableObserver = Observer<Int> {
        if (viewModel.images.size == it) rvImages.clearOnScrollListeners()
    }

    private val networkAvailableObserver = Observer<Boolean> {
        if (it == false) {
            rvImages.clearOnScrollListeners()
            viewModel.snackbarMessage.value = Constants.Errors.noNetwork
        } else rvImages.addOnScrollListener(scrollListener)
    }

    private val snackbarMessageObserver = Observer<Any> { rvImages.snackLong(it) }

    private val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val lastVisiblePosition = (recyclerView.layoutManager as? GridLayoutManager)
                    ?.findLastCompletelyVisibleItemPosition()

            viewModel.pageScrolled(lastVisiblePosition ?: -1)
        }
    }

    private fun itemClickAction(position: Int) {

        Intent(context, GalleryViewPagerActivity::class.java)
                .arguments { putInt(Constants.Keys.position, position) }
                .also { startActivityForResult(it, Constants.RequestCodes.galleryViewPagerActivity) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) when (requestCode) {

            Constants.RequestCodes.galleryViewPagerActivity ->
                data?.getIntExtra(Constants.Keys.position, Constants.defaultPosition)
                        ?.let { rvImages.scrollToPosition(it) }
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

            images.addOnListChangedCallback(imagesObserver)

            maxImagesAvailable.observe(this@GalleryGridFragment, maxImagesAvailableObserver)
            networkAvailable.observe(this@GalleryGridFragment, networkAvailableObserver)
            snackbarMessage.observe(this@GalleryGridFragment, snackbarMessageObserver)
            start()
        }
    }

    private fun setupRecyclerView() {

        // Set span count according to the screen orientation
        val spanCount = if (context?.orientation == Configuration.ORIENTATION_LANDSCAPE) 6 else 4

        // Initialize recycler view
        rvImages.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, spanCount)
            adapter = GalleryGridAdapter(viewModel.images, ::itemClickAction)
            arguments?.let { scrollToPosition(it.getInt(Constants.Keys.position)) }
        }
    }

    companion object {
        fun newInstance() = GalleryGridFragment()
    }
}
