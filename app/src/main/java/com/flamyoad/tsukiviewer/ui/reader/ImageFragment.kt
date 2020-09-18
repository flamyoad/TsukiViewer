package com.flamyoad.tsukiviewer.ui.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.flamyoad.tsukiviewer.R
import kotlinx.android.synthetic.main.reader_image_item.*
import java.io.File

class ImageFragment(private val image: File): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.reader_image_item, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Glide.with(this)
            .load(image.toUri())
            .into(photoView)
    }

    companion object {
        fun newInstance(image: File): Fragment {
            return ImageFragment(image)
        }
    }
}