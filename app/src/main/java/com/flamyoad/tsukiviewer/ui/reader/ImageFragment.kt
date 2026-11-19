package com.flamyoad.tsukiviewer.ui.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.flamyoad.tsukiviewer.databinding.ReaderImageItemBinding
import java.io.File

class ImageFragment : Fragment() {
    private var pages = emptyList<Pair<ReaderPageImageView, File>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ReaderImageItemBinding.inflate(inflater, container, false)
        val args = requireArguments()
        val second = args.getString(SECOND_IMAGE_PATH)
        binding.root.layoutDirection =
            if (args.getBoolean(RIGHT_TO_LEFT)) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
        binding.secondPhotoView.isVisible = second != null
        pages = buildList {
            add(binding.photoView to File(requireNotNull(args.getString(IMAGE_PATH))))
            if (second != null) add(binding.secondPhotoView to File(second))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listener = requireActivity() as ViewPagerListener
        for ((photo, file) in pages) {
            photo.setOnTouchListener { _, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> listener.setUserInputEnabled(false)
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> listener.setUserInputEnabled(true)
                }
                false
            }
            loadImage(photo, file)
        }
    }

    override fun onResume() {
        super.onResume()
        for ((photo, file) in pages) {
            if (!photo.isImageLoaded) loadImage(photo, file)
            photo.startAnimation()
        }
    }

    override fun onPause() {
        for ((photo, _) in pages) photo.stopAnimation()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        for ((photo, _) in pages) photo.recycle()
    }

    override fun onDestroyView() {
        pages = emptyList()
        super.onDestroyView()
    }

    private fun loadImage(photo: ReaderPageImageView, image: File) {
        photo.transitionName = ReaderActivity.pageTransitionName(image)
        photo.setOnImageEventListener(object : SubsamplingScaleImageView.DefaultOnImageEventListener() {
            override fun onReady() {
                (activity as? ReaderActivity)?.onReaderPageReady(photo)
            }

            override fun onImageLoadError(e: Exception) {
                (activity as? ReaderActivity)?.onReaderPageError(photo)
            }
        })
        photo.setPage(image)
    }

    companion object {
        private const val IMAGE_PATH = "imagepath"
        private const val SECOND_IMAGE_PATH = "second_image_path"
        private const val RIGHT_TO_LEFT = "right_to_left"

        fun newInstance(image: File, second: File? = null, rightToLeft: Boolean = false) =
            ImageFragment().apply {
                arguments = Bundle().apply {
                    putString(IMAGE_PATH, image.path)
                    putString(SECOND_IMAGE_PATH, second?.path)
                    putBoolean(RIGHT_TO_LEFT, rightToLeft)
                }
            }
    }
}
