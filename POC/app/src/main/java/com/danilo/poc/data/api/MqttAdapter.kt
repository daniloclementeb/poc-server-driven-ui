package com.danilo.poc.data.api

import android.content.Context
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.amazonaws.regions.Regions


class MqttAdapter(user: Map<String, String>, context: Context, callback: AWSIotMqttNewMessageCallback?) {
    val mqttClient: AWSIotMqttManager = provideMqtt(
        context,
        user,
        "aifbki20jw5mg-ats.iot.us-east-1.amazonaws.com", callback
    )
    var posted = false
    private fun provideMqtt(
        context: Context,
        user: Map<String, String>,
        endpoint: String,
        callback: AWSIotMqttNewMessageCallback?
    ): AWSIotMqttManager {
        //val clientId = user
        //val CUSTOMER_SPECIFIC_ENDPOINT = "aifbki20jw5mg-ats.iot.us-east-1.amazonaws.com"
        var mqttManager = AWSIotMqttManager(user.get("token"), endpoint)
        mqttManager.setKeepAlive(10); //3 minutos conectado
        /*val credentialsProvider = CognitoCachingCredentialsProvider(
            context,
            "us-east-1:4d7d4d79-0fa2-4c56-b637-994efad12961",  // Identity pool ID
            Regions.US_EAST_1 // Region
        )*/
        // Initialize the Amazon Cognito credentials provider
        // Initialize the Amazon Cognito credentials provider
        val credentialsProvider = CognitoCachingCredentialsProvider(
            context,
            "us-east-1:ed523cff-47ec-4608-9016-8fcdc01e7608",  // Identity pool ID
            Regions.US_EAST_1 // Region
        )


        mqttManager.connect(credentialsProvider,
            AWSIotMqttClientStatusCallback { status, throwable ->

                if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connecting) {
                    System.out.println("Connecting...")
                } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected) {
                    System.out.println("Connected")
                    if (!posted) {
                        mqttClient.subscribeToTopic(
                                user.get("token"),
                                AWSIotMqttQos.QOS0, callback
                        )
                        mqttClient.publishString("{\"token\":\"" + user.get("token") + "\", \"city\":\""+user.get("city")+"\"}", "logged", AWSIotMqttQos.QOS0)
                        posted = true
                    }
                } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Reconnecting) {
                    if (throwable != null) {
                        System.out.println("Connection error.")
                    }
                    System.out.println("Reconnecting")
                } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost) {
                    if (throwable != null) {
                        System.out.println("Connection error.")
                        throwable.printStackTrace()
                    }
                    System.out.println("Disconnected")
                } else {
                    System.out.println("Disconnected")
                }
            })
            return mqttManager
    }

}