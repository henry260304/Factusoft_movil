package com.tuempresa.factusoft

import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import android.util.Log

/**
 * Servicio de API para Categorías de Productos
 * Endpoint: https://factusoft-backend-2025-cndzh3e6cxcvdnch.northcentralus-01.azurewebsites.net/Catalogos/Product_Category/
 */
class CategoryApiService {
    
    private val baseUrl = "https://factusoft-backend-2025-cndzh3e6cxcvdnch.northcentralus-01.azurewebsites.net/Catalogos/Product_Category/"
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    
    interface ApiCallback<T> {
        fun onSuccess(data: T)
        fun onError(error: String)
    }
    
    /**
     * Obtener todas las categorías
     */
    fun getAllCategories(callback: ApiCallback<List<Category>>) {
        Log.d("CategoryApiService", "getAllCategories iniciado")
        Log.d("CategoryApiService", "URL: $baseUrl")
        
        val request = Request.Builder()
            .url(baseUrl)
            .get()
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("CategoryApiService", "onFailure: ${e.message}", e)
                callback.onError("Error de conexión: ${e.message}")
            }
            
            override fun onResponse(call: Call, response: Response) {
                Log.d("CategoryApiService", "onResponse - Código: ${response.code}")
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("CategoryApiService", "Respuesta no exitosa: ${response.code}")
                        callback.onError("Error ${response.code}: ${response.message}")
                        return
                    }
                    
                    try {
                        val jsonData = response.body?.string()
                        Log.d("CategoryApiService", "JSON recibido: $jsonData")
                        
                        val categoryResponse = gson.fromJson(jsonData, CategoryResponse::class.java)
                        Log.d("CategoryApiService", "Categorías procesadas: ${categoryResponse.results.size}")
                        callback.onSuccess(categoryResponse.results)
                    } catch (e: Exception) {
                        Log.e("CategoryApiService", "Error al procesar JSON: ${e.message}", e)
                        callback.onError("Error al procesar datos: ${e.message}")
                    }
                }
            }
        })
    }
}


