package com.example.rxjava_flatmap_example.request

import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory

import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://jsonplaceholder.typicode.com/"

class ServiceGenerator {
    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())

    private val retrofit = retrofitBuilder.build()

    private val requestApi = retrofit.create(RequestApi::class.java)

    fun getRequestApi(): RequestApi {
        return requestApi
    }
}