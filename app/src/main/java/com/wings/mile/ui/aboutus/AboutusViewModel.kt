package com.wings.mile.ui.aboutus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AboutusViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
//        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text
}