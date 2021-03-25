package com.flamyoad.tsukiviewer.ui.reader

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.davemorrissey.labs.subscaleview.ImageSource
import com.flamyoad.tsukiviewer.R
import kotlinx.android.synthetic.main.reader_image_item.*
import java.io.File

private const val IMAGE_PATH = "imagepath"

class ImageFragment : Fragment() {

    private var viewPagerListener: ViewPagerListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.reader_image_item, container, false)
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
        photoView.setOnTouchListener { view, motionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    viewPagerListener?.setUserInputEnabled(false)
                    return@setOnTouchListener true
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
        if (!photoView.isImageLoaded) {
            loadImage()
        }
    }

    override fun onStop() {
        super.onStop()
        photoView.recycle()
    }

    fun loadImage() {
        val imagePath = arguments?.getString(IMAGE_PATH)
        val image = File(imagePath)
        photoView.setImage(ImageSource.uri(Uri.fromFile(image)))
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