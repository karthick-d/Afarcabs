package com.wings.mile.ui.refer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReferViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Refer a Friend Page"
    }
    val text: LiveData<String> = _text
}