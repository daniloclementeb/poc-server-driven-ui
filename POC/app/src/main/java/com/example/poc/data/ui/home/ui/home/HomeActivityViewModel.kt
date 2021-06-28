package com.example.poc.data.ui.home.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback
import com.example.poc.data.HomeRepository
import com.example.poc.data.ui.home.GenericFormState
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class HomeActivityViewModel(private val homeRepository: HomeRepository) : ViewModel() {

    private val _navForm = MutableLiveData<GenericFormState>()
    val navFormState: LiveData<GenericFormState> = _navForm
    private val _navResult = MutableLiveData<Result<Map<String, Object>>>()
    val navResult: LiveData<Result<Map<String, Object>>> = _navResult

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
    var mapa: HashMap<String, Object> = HashMap<String, Object>()

    private val _hash = MutableLiveData<Result<HashMap<String, Object>>>()
    val hash: LiveData<Result<HashMap<String, Object>>> = _hash

    fun createMapa(novoMapa:Map<String, Object>) {
        novoMapa.forEach { key, objeto ->
            if (mapa.get(key) != null) {
                var map = objeto as Map<String, Object>
                var item = mapa.get(key) as Map<String, Object>
                if (map.get("clear") != null && map.get("clear") as String == "true") {
                    mapa.set(key, map as Object)
                    (mapa.get(key) as HashMap<String, Object>).put("updated", "true" as Object)
                } else {
                    var list = (mapa.get(key) as Map<String, Object>).get("list") as ArrayList<Object>
                    //var plus = list.plus(item.get("list"))
                    var union = ArrayList<Object>((list).union(map.get("list") as ArrayList<Object>))
                    if (list.size != union.size) {
                        (mapa.get(key) as HashMap<String, Object>).put("updated", "true" as Object)
                    }
                    (mapa.get(key) as HashMap<String, ArrayList<Object>>).set("list", union as ArrayList<Object>)

                }
            } else {
                //nao tinha esse campo antes
                mapa.put(key, objeto)
                (mapa.get(key) as HashMap<String, Object>).put("updated", "true" as Object)

            }
        }.also {
            _hash.postValue(Result.success(mapa))
        }
    }

    suspend fun subscribeHomeTopic(username: String, context: Context) {
        val result = homeRepository.subscribe(username, context, AWSIotMqttNewMessageCallback() { s: String, data: ByteArray ->
            //topic e data
            System.out.println("DANILOOOOW " + String(data))

            val JSON = jacksonObjectMapper() // creates ObjectMapper() and adds Kotlin module in one step

            val novoMapa: Map<String, Object> = JSON.readValue(data)
            //trata Hash de cada item
            createMapa(novoMapa)

            //trata tabbar
            _navResult.postValue(Result.success(mapa))
        })
    }

    fun disconnect(username: String,
                   context: Context) {
        homeRepository.disconnect(username, context)
    }
}