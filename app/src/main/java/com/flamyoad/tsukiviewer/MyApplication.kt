package com.flamyoad.tsukiviewer

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.bumptech.glide.Glide
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.utils.toast
import com.jakewharton.threetenabp.AndroidThreeTen


class MyApplication : Application() {

    var fullDoujinList: MutableList<Doujin>? = null

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        /*
        https://stackoverflow.com/questions/42251634/android-os-fileuriexposedexception-file-jpg-exposed-beyond-app-through-clipdata

        If the uri starts with content://, then the third party gallery cannot access any other images in the same directory

        This code is used to ignore the file checking so that we can pass uri with file:// in intent

        When passing file:// uri, the third party app is free to access every file inside the directory
        */
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Glide.with(this).onTrimMemory(TRIM_MEMORY_MODERATE)
    }
}