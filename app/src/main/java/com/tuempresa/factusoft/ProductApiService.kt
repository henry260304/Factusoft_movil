package com.tuempresa.factusoft

import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import android.util.Log

/**
 * Servicio de API para Productos
 * Endpoint: https://factusoft-backend-2025-cndzh3e6cxcvdnch.northcentralus-01.azurewebsites.net/Operaciones/Product/
 */
class ProductApiService {
    
    private val baseUrl = "https://factusoft-backend-2025-cndzh3e6cxcvdnch.northcentralus-01.azurewebsites.net/Operaciones/Product/"
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
     * Obtener todos los productos (primera página)
     */
    fun getAllProducts(callback: ApiCallback<List<Product>>) {
        Log.d("ProductApiService", "getAllProducts iniciado")
        Log.d("ProductApiService", "URL: $baseUrl")
        
        val request = Request.Builder()
            .url(baseUrl)
            .get()
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ProductApiService", "onFailure: ${e.message}", e)
                callback.onError("Error de conexión: ${e.message}")
            }
            
            override fun onResponse(call: Call, response: Response) {
                Log.d("ProductApiService", "onResponse - Código: ${response.code}")
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("ProductApiService", "Respuesta no exitosa: ${response.code}")
                        callback.onError("Error ${response.code}: ${response.message}")
                        return
                    }
                    
                    try {
                        val jsonData = response.body?.string()
                        Log.d("ProductApiService", "JSON recibido (primeros 150 chars): ${jsonData?.take(150)}")
                        
                        val productResponse = gson.fromJson(jsonData, ProductResponse::class.java)
                        Log.d("ProductApiService", "Productos procesados: ${productResponse.results.size} de ${productResponse.count}")
                        callback.onSuccess(productResponse.results)
                    } catch (e: Exception) {
                        Log.e("ProductApiService", "Error al procesar JSON: ${e.message}", e)
                        callback.onError("Error al procesar datos: ${e.message}")
                    }
                }
            }
        })
    }
    
    /**
     * Crear un nuevo producto
     */
    fun createProduct(product: NewProduct, callback: ApiCallback<Product>) {
        Log.d("ProductApiService", "createProduct iniciado")
        
        val jsonBody = gson.toJson(product)
        Log.d("ProductApiService", "JSON a enviar: $jsonBody")
        
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonBody.toRequestBody(mediaType)
        
        val request = Request.Builder()
            .url(baseUrl)
            .post(requestBody)
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ProductApiService", "onFailure: ${e.message}", e)
                callback.onError("Error de conexión: ${e.message}")
            }
            
            override fun onResponse(call: Call, response: Response) {
                Log.d("ProductApiService", "onResponse - Código: ${response.code}")
                response.use {
                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string()
                        Log.e("ProductApiService", "Error del servidor: $errorBody")
                        callback.onError("Error ${response.code}: $errorBody")
                        return
                    }
                    
                    try {
                        val jsonData = response.body?.string()
                        Log.d("ProductApiService", "Respuesta: $jsonData")
                        val newProduct = gson.fromJson(jsonData, Product::class.java)
                        callback.onSuccess(newProduct)
                    } catch (e: Exception) {
                        Log.e("ProductApiService", "Error al procesar respuesta: ${e.message}", e)
                        callback.onError("Error al procesar respuesta: ${e.message}")
                    }
                }
            }
        })
    }
    
    /**
     * Actualizar un producto existente
     */
    fun updateProduct(product: Product, callback: ApiCallback<Product>) {
        val jsonBody = gson.toJson(product)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonBody.toRequestBody(mediaType)
        
        val request = Request.Builder()
            .url("$baseUrl${product.idProduct}/")
            .put(requestBody)
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }
            
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        callback.onError("Error ${response.code}: ${response.message}")
                        return
                    }
                    
                    try {
                        val jsonData = response.body?.string()
                        val updatedProduct = gson.fromJson(jsonData, Product::class.java)
                        callback.onSuccess(updatedProduct)
                    } catch (e: Exception) {
                        callback.onError("Error al procesar respuesta: ${e.message}")
                    }
                }
            }
        })
    }
    
    /**
     * Eliminar un producto
     */
    fun deleteProduct(productId: Int, callback: ApiCallback<Boolean>) {
        val request = Request.Builder()
            .url("$baseUrl$productId/")
            .delete()
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }
            
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        callback.onError("Error ${response.code}: ${response.message}")
                        return
                    }
                    
                    callback.onSuccess(true)
                }
            }
        })
    }
}

