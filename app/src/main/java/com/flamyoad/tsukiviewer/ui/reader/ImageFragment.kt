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

        val bundle = requireActivity().intent.extras
        val imageTransitionName = bundle?.getString(DoujinImagesAdapter.TRANSITION_NAME) ?: ""

        photoView.transitionName = imageTransitionName

        Glide.with(this)
            .load(image.toUri())
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    this@ImageFragment.requireActivity().supportPostponeEnterTransition()
                    return true
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    this@ImageFragment.requireActivity().supportPostponeEnterTransition()
                    return false
                }

            })
            .into(photoView)
    }

    companion object {
        fun newInstance(image: File): Fragment {
            return ImageFragment(image)
        }
    }
}