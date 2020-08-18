package com.flamyoad.tsukiviewer.utils

import androidx.lifecycle.MutableLiveData

// https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata

class LiveDataExtensions {
    fun <T> MutableLiveData<MutableList<T>>.addNewItem(item: T) {
        val oldValue = this.value ?: mutableListOf()
        oldValue.add(item)
        this.value = oldValue
    }

    fun <T> MutableLiveData<MutableList<T>>.addNewItemAt(index: Int, item: T) {
        val oldValue = this.value ?: mutableListOf()
        oldValue.add(index, item)
        this.value = oldValue
    }

    fun <T> MutableLiveData<MutableList<T>>.removeItemAt(index: Int) {
        if (!this.value.isNullOrEmpty()) {
            val oldValue = this.value
            oldValue?.removeAt(index)
            this.value = oldValue
        } else {
            this.value = mutableListOf()
        }
    }
}