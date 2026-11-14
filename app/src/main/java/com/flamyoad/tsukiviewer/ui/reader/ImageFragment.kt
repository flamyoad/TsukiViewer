package com.flamyoad.tsukiviewer.ui.reader

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.flamyoad.tsukiviewer.databinding.ReaderImageItemBinding
import java.io.File

private const val IMAGE_PATH = "imagepath"

class ImageFragment : Fragment() {

    private var _binding: ReaderImageItemBinding? = null
    private val binding get() = _binding!!

    private var viewPagerListener: ViewPagerListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ReaderImageItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadImage()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewPagerListener = context as ViewPagerListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Only works for Horizontal image fragment. There is a separate method for Vertical image since its a RecyclerView
        binding.photoView.setOnTouchListener { view, motionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    viewPagerListener?.setUserInputEnabled(false)
                }
                MotionEvent.ACTION_UP -> {
                    viewPagerListener?.setUserInputEnabled(true)
                }
                MotionEvent.ACTION_CANCEL -> { // Actually triggers much more than ACTION_UP, why?
                    viewPagerListener?.setUserInputEnabled(true)
                }
            }
            return@setOnTouchListener false
        }
    }

    override fun onResume() {
        super.onResume()
        if (!binding.photoView.isImageLoaded) {
            loadImage()
        }
    }

    override fun onStop() {
        super.onStop()
        binding.photoView.recycle()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun loadImage() {
        val imagePath = arguments?.getString(IMAGE_PATH) ?: return
        val image = File(imagePath)
        val photo = binding.photoView
        photo.transitionName = ReaderActivity.pageTransitionName(image)
        photo.setOnImageEventListener(object : SubsamplingScaleImageView.DefaultOnImageEventListener() {
            override fun onReady() {
                (activity as? ReaderActivity)?.onReaderPageReady(photo)
            }

            override fun onImageLoadError(e: Exception) {
                (activity as? ReaderActivity)?.onReaderPageError(photo)
            }
        })
        photo.setImage(ImageSource.uri(Uri.fromFile(image)))
    }

    companion object {
        fun newInstance(image: File): Fragment {
            return ImageFragment().apply {
                arguments = Bundle().apply {
                    putString(IMAGE_PATH, image.path)
                }
            }
        }
    }
}