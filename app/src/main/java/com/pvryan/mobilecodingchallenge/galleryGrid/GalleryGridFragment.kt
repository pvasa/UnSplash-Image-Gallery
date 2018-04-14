package com.pvryan.mobilecodingchallenge.galleryGrid

import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.res.Configuration
import android.databinding.ObservableList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.R
import com.pvryan.mobilecodingchallenge.data.models.Image
import com.pvryan.mobilecodingchallenge.extensions.getOrientation
import com.pvryan.mobilecodingchallenge.extensions.snackLong
import com.pvryan.mobilecodingchallenge.galleryViewPager.GalleryViewPagerActivity
import kotlinx.android.synthetic.main.fragment_gallery_grid.*

class GalleryGridFragment : Fragment() {

    private lateinit var viewModel: GalleryViewModel
    private val imagesCallback by lazy {
        object : ObservableList.OnListChangedCallback<ObservableList<Image>>() {
            override fun onChanged(list: ObservableList<Image>) {
                rvImages.adapter.notifyDataSetChanged()
            }

            override fun onItemRangeRemoved(list: ObservableList<Image>,
                                            positionStart: Int, itemCount: Int) {
                rvImages.adapter.notifyItemRangeRemoved(positionStart, itemCount)
            }

            override fun onItemRangeMoved(list: ObservableList<Image>,
                                          p1: Int, p2: Int, p3: Int) {
            }

            override fun onItemRangeInserted(
                    list: ObservableList<Image>, positionStart: Int, itemCount: Int) {
                viewModel.preloadImages(positionStart)
                rvImages.adapter.notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeChanged(list: ObservableList<Image>,
                                            positionStart: Int, itemCount: Int) {
                rvImages.adapter.notifyItemRangeInserted(positionStart, itemCount)
            }
        }
    }
    private val lastVisiblePositionCallback by lazy {
        Observer<Int> {
            if (it == viewModel.images.size - 1)
                viewModel.loadImageUrls(
                        (viewModel.images.size / Constants.imagesPerPage) + 1)
        }
    }
    private val viewPagerPositionCallback by lazy {
        Observer<Int> {
            rvImages.scrollToPosition(it ?: 0)
        }
    }
    private val maxImagesAvailableCallback by lazy {
        Observer<Int> {
            if (viewModel.images.size == it)
                rvImages.clearOnScrollListeners()
        }
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
        GalleryRVScrollListener {
            viewModel.lastVisiblePosition.value = it
        }
    }

    private val imageItemUserActionListener = object : ImageItemUserActionListener {
        override fun onImageClick(position: Int) {

            val args = Bundle()
            args.putInt(Constants.Keys.position, position)
            /*val galleryPagerFragment = GalleryViewPagerFragment.newInstance(args)
            activity?.supportFragmentManager?.beginTransaction()
                    ?.add(R.id.container, galleryPagerFragment, GalleryViewPagerFragment.tag)
                    ?.addToBackStack(GalleryViewPagerFragment.tag)
                    ?.commit()*/

            val intent = Intent(activity, GalleryViewPagerActivity::class.java)
            intent.putExtras(args)
            startActivityForResult(intent, Constants.RequestCodes.viewPagerActivity)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = (activity as GalleryGridActivity).obtainViewModel()
        return inflater.inflate(R.layout.fragment_gallery_grid, container, false)
    }

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
        super.onPause()
        rvImages.clearOnScrollListeners()
    }

    private fun setupViewModel() {
        with(viewModel) {
            viewModel.images.addOnListChangedCallback(imagesCallback)
            lastVisiblePosition.observe(
                    this@GalleryGridFragment, lastVisiblePositionCallback)
            viewPagerPosition.observe(
                    this@GalleryGridFragment, viewPagerPositionCallback)
            maxImagesAvailable.observe(
                    this@GalleryGridFragment, maxImagesAvailableCallback)
            networkAvailable.observe(
                    this@GalleryGridFragment, networkAvailableCallback)
            snackbarMessage.observe(
                    this@GalleryGridFragment, snackbarMessageObserver)
            start()
        }
    }

    private fun setupRecyclerView() {
        // Set span count according to the screen orientation
        val spanCount = if (activity != null &&
                (activity as AppCompatActivity).getOrientation() ==
                Configuration.ORIENTATION_LANDSCAPE) 6 else 4
        // Initialize recycler view
        rvImages.setHasFixedSize(true)
        rvImages.layoutManager = GridLayoutManager(activity, spanCount)
        rvImages.adapter = GalleryGridAdapter(viewModel.images, imageItemUserActionListener)
        arguments?.let {
            rvImages.scrollToPosition(it.getInt(Constants.Keys.position))
        }
    }

    companion object {
        val tag: String = GalleryGridFragment::class.java.simpleName
        fun newInstance(args: Bundle? = null): GalleryGridFragment {
            val galleryGridFragment = GalleryGridFragment()
            galleryGridFragment.arguments = args
            return galleryGridFragment
        }
    }
}
