package com.yoohayoung.allwrite.ui.friend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FriendViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Friend Fragment"
    }
    val text: LiveData<String> = _text
}