package com.example.poc.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class HomeRepository(val dataSource: HomeDataSource) {

    // in-memory cache of the loggedInUser object
    var user: Map<String, Object>? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    suspend fun subscribe(
        username: String,
        context: Context,
        callback: AWSIotMqttNewMessageCallback
    ) {
        dataSource.subscribe(username, context, callback)
    }

    private fun setLoggedInUser(loggedInUser: Map<String, Object>?) {
        loggedInUser.also { it -> this.user = it }
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    fun disconnect(username: String,
                   context: Context) {
        dataSource.disconnect(username, context)
    }
}