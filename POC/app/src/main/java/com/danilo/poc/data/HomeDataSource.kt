package com.danilo.poc.data

import android.content.Context
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback
import com.danilo.poc.data.api.MqttAdapter

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class HomeDataSource {

    suspend fun subscribe(
            username: Map<String, String>,
            context: Context,
            callback: AWSIotMqttNewMessageCallback
    ) {
        val mqttAdapter = MqttAdapter(username, context, callback)
     }

    fun logout() {
        // TODO: revoke authentication
    }

    fun disconnect( username: Map<String, String>,
                    context: Context) {
        MqttAdapter(username, context, null).mqttClient.disconnect()
    }
}