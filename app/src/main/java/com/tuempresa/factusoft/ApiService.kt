package com.tuempresa.factusoft

import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import android.util.Log

/**
 * Servicio de API para Clientes
 * Endpoint: https://factusoft-backend-2025-cndzh3e6cxcvdnch.northcentralus-01.azurewebsites.net/Catalogos/Customer/
 */
class ApiService {
    
    private val baseUrl = "https://factusoft-backend-2025-cndzh3e6cxcvdnch.northcentralus-01.azurewebsites.net/Catalogos/Customer/"
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
     * Obtener todos los clientes (con paginación)
     */
    fun getAllCustomers(callback: ApiCallback<List<Customer>>) {
        val request = Request.Builder()
            .url(baseUrl)
            .get()
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
                        val customerResponse = gson.fromJson(jsonData, CustomerResponse::class.java)
                        callback.onSuccess(customerResponse.results)
                    } catch (e: Exception) {
                        callback.onError("Error al procesar datos: ${e.message}")
                    }
                }
            }
        })
    }
    
    /**
     * Obtener todos los clientes de todas las páginas
     */
    fun getAllCustomersPaginated(callback: ApiCallback<List<Customer>>) {
        Log.d("ApiService", "getAllCustomersPaginated iniciado")
        Log.d("ApiService", "URL Base: $baseUrl")
        
        val allCustomers = mutableListOf<Customer>()
        
        fun fetchPage(url: String) {
            Log.d("ApiService", "fetchPage: $url")
            
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            
            Log.d("ApiService", "Ejecutando request...")
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("ApiService", "onFailure: ${e.message}", e)
                    callback.onError("Error de conexión: ${e.message}")
                }
                
                override fun onResponse(call: Call, response: Response) {
                    Log.d("ApiService", "onResponse - Código: ${response.code}")
                    response.use {
                        if (!response.isSuccessful) {
                            Log.e("ApiService", "Respuesta no exitosa: ${response.code} - ${response.message}")
                            callback.onError("Error ${response.code}: ${response.message}")
                            return
                        }
                        
                        try {
                            val jsonData = response.body?.string()
                            Log.d("ApiService", "JSON recibido (primeros 200 chars): ${jsonData?.take(200)}")
                            
                            val customerResponse = gson.fromJson(jsonData, CustomerResponse::class.java)
                            allCustomers.addAll(customerResponse.results)
                            
                            Log.d("ApiService", "Página procesada: ${customerResponse.results.size} clientes. Total acumulado: ${allCustomers.size}")
                            Log.d("ApiService", "Next page: ${customerResponse.next}")
                            
                            // Si hay más páginas, continuar
                            if (customerResponse.next != null) {
                                fetchPage(customerResponse.next)
                            } else {
                                // Ya no hay más páginas, retornar todos
                                Log.d("ApiService", "Todas las páginas cargadas. Total: ${allCustomers.size} clientes")
                                callback.onSuccess(allCustomers)
                            }
                        } catch (e: Exception) {
                            Log.e("ApiService", "Error al procesar JSON: ${e.message}", e)
                            callback.onError("Error al procesar datos: ${e.message}")
                        }
                    }
                }
            })
        }
        
        fetchPage(baseUrl)
    }
    
    /**
     * Crear un nuevo cliente
     */
    fun createCustomer(customer: NewCustomer, callback: ApiCallback<Customer>) {
        val jsonBody = gson.toJson(customer)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonBody.toRequestBody(mediaType)
        
        val request = Request.Builder()
            .url(baseUrl)
            .post(requestBody)
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
                        val newCustomer = gson.fromJson(jsonData, Customer::class.java)
                        callback.onSuccess(newCustomer)
                    } catch (e: Exception) {
                        callback.onError("Error al procesar respuesta: ${e.message}")
                    }
                }
            }
        })
    }
    
    /**
     * Actualizar un cliente existente (PUT)
     */
    fun updateCustomer(customerId: Int, customer: NewCustomer, callback: ApiCallback<Customer>) {
        val jsonBody = gson.toJson(customer)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonBody.toRequestBody(mediaType)
        
        val request = Request.Builder()
            .url("$baseUrl$customerId/")
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
                        val updatedCustomer = gson.fromJson(jsonData, Customer::class.java)
                        callback.onSuccess(updatedCustomer)
                    } catch (e: Exception) {
                        callback.onError("Error al procesar respuesta: ${e.message}")
                    }
                }
            }
        })
    }
    
    /**
     * Actualizar un cliente existente (PATCH) - método alternativo
     */
    fun updateCustomerPatch(customerId: Int, customer: NewCustomer, callback: ApiCallback<Customer>) {
        val jsonBody = gson.toJson(customer)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonBody.toRequestBody(mediaType)
        
        val request = Request.Builder()
            .url("$baseUrl$customerId/")
            .patch(requestBody)
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
                        val updatedCustomer = gson.fromJson(jsonData, Customer::class.java)
                        callback.onSuccess(updatedCustomer)
                    } catch (e: Exception) {
                        callback.onError("Error al procesar respuesta: ${e.message}")
                    }
                }
            }
        })
    }
    
    /**
     * Eliminar un cliente
     */
    fun deleteCustomer(customerId: Int, callback: ApiCallback<Boolean>) {
        val request = Request.Builder()
            .url("$baseUrl$customerId/")
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

