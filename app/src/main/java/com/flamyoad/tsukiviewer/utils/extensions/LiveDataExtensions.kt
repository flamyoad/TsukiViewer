package com.flamyoad.tsukiviewer.utils.extensions

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.forceRefresh() {
    this.value = this.value
}
