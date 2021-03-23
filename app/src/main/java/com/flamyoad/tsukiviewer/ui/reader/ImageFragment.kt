package com.flamyoad.tsukiviewer.ui.reader

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.davemorrissey.labs.subscaleview.ImageSource
import com.flamyoad.tsukiviewer.R
import kotlinx.android.synthetic.main.reader_image_item.*
import java.io.File

private const val IMAGE_PATH = "imagepath"

class ImageFragment : Fragment(), ReaderFragmentListener {

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

    override fun clearMemory() {
        photoView.recycle()
    }
}