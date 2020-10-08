package com.flamyoad.tsukiviewer.ui.reader

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter
import kotlinx.android.synthetic.main.reader_image_item.*
import java.io.File

private const val IMAGE_PATH = "imagepath"

class ImageFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.reader_image_item, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val imagePath = arguments?.getString(IMAGE_PATH)

        val image = File(imagePath)

        Glide.with(this)
            .load(image.toUri())
            .into(photoView)
    }

    override fun onPause() {
        super.onPause()
        photoView.scale = 1.0f // Resets the zoom scale. todo: Perhaps put this in Settings for users to choose whether to...?
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