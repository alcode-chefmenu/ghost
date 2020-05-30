package com.example.nami.controllers.services

import SectionFragment
import android.util.Log
import com.example.nami.models.auth.LoginRequest
import com.example.nami.models.auth.LoginResponse
import com.example.nami.models.detailModels.*
import com.example.nami.models.sections.SectionsResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


class ServiceInteractor : ServiceFactory() {

    private var viewModelJob: Job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    fun postLogin(
        user: String,
        password: String,
        then: (LoginResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            postLogins(user, password, then, error)
        }
    }

    private suspend fun postLogins(
        user: String,
        password: String,
        then: (LoginResponse) -> Unit,
        error: (String) -> Unit
    ) {
        val url = serverUrl + routeBase + routeAuth + routeLogin
        val request = LoginRequest(user, password)
        val json = Gson().toJson(request)
        withContext(Dispatchers.IO) {
            post(url, json).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()

                    val gson = GsonBuilder().create()
                    val res = gson.fromJson(body, LoginResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                        //Log.i("respuesta",response.message)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.i("Error", e.message.toString())
                    error("Error en el servicio")
                }
            })
        }

    }

    fun getSections(
        token: String,
        then: (SectionsResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            getSectionsCorrutine(token, then, error)
        }
    }

    fun getSectionsCorrutine(
        token: String,
        then: (SectionsResponse) -> Unit,
        error: (String) -> Unit
    ) {

        val url = serverUrl + routeBase + routeSections
        get(url, token).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()

                val gson = GsonBuilder().create()
                val res = gson.fromJson(body, SectionsResponse::class.java)
                if (response.isSuccessful) {
                    then(res)
                } else {
                    error(res.message.toString())
                    //Log.i("respuesta",response.message)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.i("Error", e.message.toString())
                error("Error en el servicio")
            }
        })
    }

    fun getSection(
        token: String,
        section: Long,
        then: (SectionFragment) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            getSectionCorutine(token, section, then, error)
        }
    }

    private suspend fun getSectionCorutine(
        token: String,
        section: Long,
        then: (SectionFragment) -> Unit,
        error: (String) -> Unit
    ) {

        val url = "$serverUrl$routeBase$routeSections/$section"
        withContext(Dispatchers.IO) {
            get(url, token).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {

                    val body = response.body?.string()
                    Log.i("info sin parsear", body.toString())
                    val gson = GsonBuilder().create()
                    val res = gson.fromJson(body, SectionFragment::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.i("Error", e.message.toString())
                    error("Error en el servicio")
                }
            })
        }

    }

    fun getDetail(
        token: String,
        order: Long,
        then: (DetailResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            getDetailCorutine(token, order, then, error)
        }
    }

    private suspend fun getDetailCorutine(
        token: String,
        order: Long,
        then: (DetailResponse) -> Unit,
        error: (String) -> Unit
    ) {
        Log.i("token peticion detail", token)
        val url = "$serverUrl$routeBase$routeOrders/$order"
        withContext(Dispatchers.IO) {
            get(url, token).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {

                    val body = response.body?.string()

                    Log.i("info detail sin parsear", body.toString())
                    val gson = GsonBuilder().create()
                    val res = gson.fromJson(body, DetailResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                        //Log.i("respuesta",response.message)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.i("Error", e.message.toString())
                    error("Error en el servicio")
                }
            })
        }

    }


    fun postTakeOrder(
        token:String,
        idOrder: Long,
        dataTake: String,
        then: (TakeOrderResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            postTakeOrdercorutine(token,idOrder, dataTake, then, error)
        }
    }

    private suspend fun postTakeOrdercorutine(
        token:String,
        idOrder: Long,
        dataTake: String,
        then: (TakeOrderResponse) -> Unit,
        error: (String) -> Unit
    ) {
        val url = serverUrl + routeBase + routeOrders + idOrder + routeTake
        val request = TakeOrderRequest(dataTake)
        val json = Gson().toJson(request)
        Log.i("jsonTake",json)
        withContext(Dispatchers.IO) {
            postWithToken(token,url, json).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    Log.i("takeservice",body.toString())
                    val gson = GsonBuilder().create()
                    val res = gson.fromJson(body, TakeOrderResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                        //Log.i("respuesta",response.message)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.i("Error", e.message.toString())
                    error("Error en el servicio")
                }
            })
        }

    }

    fun putReleaseOrder(
        token: String,
        idOrder: Long,
        observations: String,
        then: (ReleaseOrderResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            putReleaseOrderCorutine(token, idOrder, observations, then, error)
        }
    }

    private suspend fun putReleaseOrderCorutine(
        token: String,
        idOrder: Long,
        observations: String,
        then: (ReleaseOrderResponse) -> Unit,
        error: (String) -> Unit
    ) {
        val url = serverUrl + routeBase + routeOrders + idOrder + routeRelease
        val request = ReleaseOrderRequest(observations)
        val json = Gson().toJson(request)
        withContext(Dispatchers.IO) {
            put(url, token, json).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()

                    val gson = GsonBuilder().create()
                    val res = gson.fromJson(body, ReleaseOrderResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                        //Log.i("respuesta",response.message)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.i("Error", e.message.toString())
                    error("Error en el servicio")
                }
            })
        }

    }

    fun postPickingOrder(
        listDataPicker: List<ListDataPicker>,
        idOrder: Long,
        idUser: Long,
        productosok: Boolean,
        totalPicker: String,
        observations: String,
        then: (PickingOrderResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            postPickingOrderCorutine(
                listDataPicker,
                idOrder,
                idUser,
                productosok,
                totalPicker,
                observations,
                then,
                error
            )
        }
    }

    private suspend fun postPickingOrderCorutine(
        listDataPicker: List<ListDataPicker>,
        idOrder: Long,
        idUser: Long,
        productosok: Boolean,
        totalPicker: String,
        observations: String,
        then: (PickingOrderResponse) -> Unit,
        error: (String) -> Unit
    ) {
        val url = serverUrl + routeBase + routePicker + routePicking
        val request = PickingOrderRequest(
            listDataPicker,
            idOrder,
            idUser,
            productosok,
            totalPicker,
            observations
        )
        val json = Gson().toJson(request)
        withContext(Dispatchers.IO) {
            post(url, json).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()

                    val gson = GsonBuilder().create()
                    val res = gson.fromJson(body, PickingOrderResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                        //Log.i("respuesta",response.message)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.i("Error", e.message.toString())
                    error("Error en el servicio")
                }
            })
        }

    }


    fun putDeliverCourier(
        token: String,
        idOrder: Long,
        then: (DeliverCourierResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            putDeliverCourierCorutine(token, idOrder, then, error)
        }
    }


    private suspend fun putDeliverCourierCorutine(
        token: String,
        idOrder: Long,
        then: (DeliverCourierResponse) -> Unit,
        error: (String) -> Unit
    ) {
        val url = serverUrl + routeBase + routeOrders + idOrder + routeDeliverCourier
        val request = DeliverCourierRequest(idOrder)
        val json = Gson().toJson(request)
        withContext(Dispatchers.IO) {
            put(token, url, json).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()

                    val gson = GsonBuilder().create()
                    val res = gson.fromJson(body, DeliverCourierResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                        //Log.i("respuesta",response.message)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.i("Error", e.message.toString())
                    error("Error en el servicio")
                }
            })
        }

    }

    fun putDeliverCustomer(
        token: String,
        idOrder: Long,
        then: (DeliverConsumerResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            putDeliverCustomerCorutine(token, idOrder, then, error)
        }
    }

    private suspend fun putDeliverCustomerCorutine(
        token: String,
        idOrder: Long,
        then: (DeliverConsumerResponse) -> Unit,
        error: (String) -> Unit
    ) {
        val url = serverUrl + routeBase + routePicker + idOrder + routeDeliverConsumer
        val request = DeliverConsumerRequest(idOrder)
        val json = Gson().toJson(request)
        withContext(Dispatchers.IO) {
            put(token, url, json).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()

                    val gson = GsonBuilder().create()
                    val res = gson.fromJson(body, DeliverConsumerResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                        //Log.i("respuesta",response.message)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.i("Error", e.message.toString())
                    error("Error en el servicio")
                }
            })
        }

    }
    fun putFreeze(
        token: String,
        idOrder: Long,
        idReason: Int,
        then: (FreezeResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            putFreezeCorutine(token, idOrder,idReason, then, error)
        }
    }

    private suspend fun putFreezeCorutine(
        token: String,
        idOrder: Long,
        idReason: Int,
        then: (FreezeResponse) -> Unit,
        error: (String) -> Unit
    ) {
        val url = serverUrl + routeBase + routePicker + idOrder + routeFreeze
        val request = FreezeRequest(idReason)
        val json = Gson().toJson(request)
        withContext(Dispatchers.IO){
            put(token, url, json).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()

                    val gson = GsonBuilder().create()
                    val res = gson.fromJson(body, FreezeResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                        //Log.i("respuesta",response.message)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.i("Error", e.message.toString())
                    error("Error en el servicio")
                }
            })
        }

    }
}