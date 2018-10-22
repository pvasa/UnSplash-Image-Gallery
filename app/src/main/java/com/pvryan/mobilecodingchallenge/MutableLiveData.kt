package com.pvryan.mobilecodingchallenge

import androidx.lifecycle.MutableLiveData

class MutableLiveData<T>(private val initialValue: T) : MutableLiveData<T>() {

    override fun getValue(): T = super.getValue() ?: initialValue
}
