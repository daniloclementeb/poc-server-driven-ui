package com.example.poc.data

import android.content.Context
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.example.poc.data.api.MqttAdapter

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class HomeDataSource {

    suspend fun subscribe(
            username: String,
            context: Context,
            callback: AWSIotMqttNewMessageCallback
    ) {
        val mqttAdapter = MqttAdapter(username, context, callback)
     }

       /*
                do {
                    mqttClient.subscribeToTopic(username,
                            AWSIotMqttQos.QOS0, callback)
                    System.out.println("Passow")
                }while (!mqttAdapter.isConnected)
    /*AWSIotMqttNewMessageCallback() { topic: String, data: ByteArray ->
                //topic e data
                System.out.println("DANILOOOOW " + String(data))
                var map = navResult.value
                if (map.isNullOrEmpty()) {
                    map = HashMap<String, Object>()
                    System.out.println("Mapa vazio")
                }
                val JSON = jacksonObjectMapper() // creates ObjectMapper() and adds Kotlin module in one step

                val novoMapa: Map<String, Object> = JSON.readValue(data)
                navResult.value = map.plus(novoMapa)



            })*/



        /*AWSIotMqttNewMessageCallback() {
        fun onMessageArrived(final String topic, final byte[] data) {
                 try {
                    String message = new String(data, "UTF-8");
                    System.out.println("Message arrived:");
                    System.out.println("   Topic: " + topic);
                    System.out.println(" Message: " + message);

                } catch (UnsupportedEncodingException e) {
                    System.out.println( "Message encoding error.", e);
                }
            }

        }
    }))


    ().mqttClient() apiClient.login(mapa)*/*/




    fun logout() {
        // TODO: revoke authentication
    }

    fun disconnect( username: String,
                    context: Context) {
        MqttAdapter(username, context, null).mqttClient.disconnect()
    }
}