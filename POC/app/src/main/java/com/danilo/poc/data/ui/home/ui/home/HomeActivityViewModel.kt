package com.danilo.poc.data.ui.home.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback
import com.danilo.poc.data.HomeRepository
import com.danilo.poc.data.ui.home.GenericFormState

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class HomeActivityViewModel(private val homeRepository: HomeRepository) : ViewModel() {

    private val _navForm = MutableLiveData<GenericFormState>()
    val navFormState: LiveData<GenericFormState> = _navForm
    private val _navResult = MutableLiveData<Result<Map<String, Object>>>()
    val navResult: LiveData<Result<Map<String, Object>>> = _navResult

    var mapa: HashMap<String, Object> = HashMap<String, Object>()

    private val _hash = MutableLiveData<Result<HashMap<String, Object>>>()
    val hash: LiveData<Result<HashMap<String, Object>>> = _hash



    suspend fun subscribeHomeTopic(map: Map<String, String>, context: Context) {
        val result = homeRepository.subscribe(map, context, AWSIotMqttNewMessageCallback() { s: String, data: ByteArray ->
            val JSON = jacksonObjectMapper() // creates ObjectMapper() and adds Kotlin module in one step

            val novoMapa: Map<String, Object> = JSON.readValue(data)
            //trata Hash de cada item
            createMapa(novoMapa)

            //trata tabbar
            _navResult.postValue(Result.success(mapa))
        })
    }

    fun disconnect(username:  Map<String, String>,
                   context: Context) {
        homeRepository.disconnect(username, context)
    }

    private val _topic = MutableLiveData<Result<HashMap<String, Object>>>()
    val topic: LiveData<Result<HashMap<String, Object>>> = _topic
    var dados: HashMap<String, Object>? = null
    suspend fun subscribeTopic(map: Map<String, String>, context: Context) {
        val result = homeRepository.subscribe(map, context, AWSIotMqttNewMessageCallback() { s: String, data: ByteArray ->
            val JSON = jacksonObjectMapper() // creates ObjectMapper() and adds Kotlin module in one step
            System.out.println(data)
            val novoMapa: Map<String, Object> = JSON.readValue(data)
            //tudo que chega vem como updated
            novoMapa.entries.forEach {
                (it.value as HashMap<String, Object>).put("updated", "true" as Object)
                var list = (it.value as HashMap<String, Object>).get("list") as ArrayList<Map<String, Object>>
                list.forEach {
                    (it as HashMap<String, Object>).put("updated", "true" as Object)
                }
            }
            if (dados == null) {
                dados = novoMapa as HashMap<String, Object>
            } else {
                //join
                createNovoDados(novoMapa)
            }
            //trata tabbar
            _topic.postValue(Result.success(dados!!))
        })
    }

    private fun createNovoDados(novoMapa: Map<String, Object>) {
        novoMapa.forEach{key, objeto ->
            if (dados?.get(key) != null) {
                //existe a chave pode nao ter o valor
                //verifica se eu tenho que limpar o campo
                var map = objeto as Map<String, Object>
                if (map.get("clear") != null && map.get("clear") as String == "true") {
                    dados?.set(key, map as Object)
                    (dados?.get(key) as HashMap<String, Object>).put("updated", "true" as Object)
                    (map.get("list") as ArrayList<Object>).forEach {
                        (it as HashMap<String, String>).put("updated", "true")
                    }
                } else {
                    //tem que fazer o join
                    //procuro se existe o id

                    var item = objeto?.get("list") as ArrayList<Object>
                    var lista = ArrayList<Object>()
                    item.forEach { it ->
                        var existe = false
                        var listItem = it as HashMap<String, Object>
                        //lista = ArrayList<Object>()
                        //lista.addAll((dados?.get(key) as HashMap<String, Object>).get("list") as ArrayList<Object>)
                        //lista = (dados?.get(key) as HashMap<String, Object>).get("list") as ArrayList<Object>//l2
                        ((dados?.get(key) as HashMap<String, Object>).get("list") as ArrayList<Object>).forEach{ it ->
                            if (((it as Map<String, Object>).get("id") as String).equals(listItem.get("id")) == true) {
                                //existe entao sobrepoem
                                //lista.remove(it)
                                lista.add(listItem as Object)
                                existe = true
                            } else {
                                if (lista.contains(it) == false)
                                    lista.add(it as Object)
                            }
                        }
                        if (!existe) {
                            lista.add(listItem as Object)
                        }
                    }
                    (dados?.get(key) as HashMap<String, Object>).put("list", lista as Object)
                    (dados?.get(key) as HashMap<String, Object>).put("updated", "true" as Object)
                }
            } else {
                //nao tinha esse campo antes
                ((objeto as HashMap<String, Object>).get("list") as ArrayList<Object>).forEach {
                    (it as HashMap<String, String>).put("updated", "true")
                }
                (objeto as HashMap<String, Object>).put("updated", "true" as Object)
                dados?.put(key, objeto)
            }
        }

    }

    fun createMapa(novoMapa:Map<String, Object>) {
        novoMapa.forEach { key, objeto ->
            if (mapa.get(key) != null) {
                var map = objeto as Map<String, Object>
                var item = mapa.get(key) as Map<String, Object>
                if (map.get("clear") != null && map.get("clear") as String == "true") {
                    mapa.set(key, map as Object)
                    (mapa.get(key) as HashMap<String, Object>).put("updated", "true" as Object)
                    (map.get("list") as ArrayList<Object>).forEach {
                        (it as HashMap<String, String>).put("updated", "true")
                    }
                } else {

                    var list = (mapa.get(key) as Map<String, Object>).get("list") as ArrayList<Object>
                    //var plus = list.plus(item.get("list"))
                    var union = ArrayList<Object>((list).union(map.get("list") as ArrayList<Object>))
                    if (list.size != union.size) {
                        (mapa.get(key) as HashMap<String, Object>).put("updated", "true" as Object)
                    }
                    (map.get("list") as ArrayList<Object>).forEach {
                        (it as HashMap<String, String>).put("updated", "true")
                    }
                    (mapa.get(key) as HashMap<String, ArrayList<Object>>).set("list", union as ArrayList<Object>)

                }
            } else {
                //nao tinha esse campo antes
                ((objeto as HashMap<String, Object>).get("list") as ArrayList<Object>).forEach {
                    (it as HashMap<String, String>).put("updated", "true")
                }
                (objeto as HashMap<String, Object>).put("updated", "true" as Object)
                mapa.put(key, objeto)


            }
        }.also {
            _hash.postValue(Result.success(mapa))
        }
    }
}