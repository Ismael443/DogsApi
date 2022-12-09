package com.ismael.dogslist

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    //Llamada a la API

    @GET
    suspend fun getDogsByBreeds(@Url url: String): Response<DogsResponse>
    //Ponemos el suspend porque es una función asincrona.
}