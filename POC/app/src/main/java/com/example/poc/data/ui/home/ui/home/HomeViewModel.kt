package com.example.poc.data.ui.home.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.poc.data.LoginRepository
import com.example.poc.data.ui.home.GenericFormState
import com.example.poc.ui.login.LoginFormState
import com.example.poc.ui.login.LoginResult

class HomeViewModel() : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun changeText(text: String) {
        _text.postValue(text)
    }
    }