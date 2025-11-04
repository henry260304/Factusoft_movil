package com.tuempresa.factusoft

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiService {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Accept", "application/json")
                .header("User-Agent", "FactuSoft-Android")
            
            // Si necesitas un token, descomenta y configura esto:
            // val token = "TU_TOKEN_AQUI"
            // requestBuilder.header("Authorization", "Bearer $token")
            
            val request = requestBuilder.build()
            
            // Log para debug
            println("=== REQUEST DEBUG ===")
            println("URL: ${request.url}")
            println("Method: ${request.method}")
            println("Headers: ${request.headers}")
            
            val response = chain.proceed(request)
            
            println("=== RESPONSE DEBUG ===")
            println("Status Code: ${response.code}")
            println("Message: ${response.message}")
            
            response
        }
        .build()
    
    private val gson = GsonBuilder().create()
    
    companion object {
        private const val BASE_URL = "https://factusoft-backend2025.azurewebsites.net"
        private const val CUSTOMERS_ENDPOINT = "$BASE_URL/Catalogos/Customer/"
    }
    
    // Callback interface para manejar respuestas
    interface ApiCallback<T> {
        fun onSuccess(data: T)
        fun onError(error: String)
    }
    
    // Obtener lista de clientes (solo primera página)
    fun getCustomers(callback: ApiCallback<List<Customer>>) {
        val request = Request.Builder()
            .url(CUSTOMERS_ENDPOINT)
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }
            
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val responseBody = it.body?.string()
                        try {
                            val customerResponse = gson.fromJson(responseBody, CustomerResponse::class.java)
                            callback.onSuccess(customerResponse.results)
                        } catch (e: Exception) {
                            callback.onError("Error al procesar respuesta: ${e.message}")
                        }
                    } else {
                        callback.onError("Error del servidor: ${it.code}")
                    }
                }
            }
        })
    }
    
    // Obtener TODOS los clientes (todas las páginas)
    fun getAllCustomers(callback: ApiCallback<List<Customer>>) {
        getAllCustomersRecursive(CUSTOMERS_ENDPOINT, mutableListOf(), callback)
    }
    
    private fun getAllCustomersRecursive(url: String, allCustomers: MutableList<Customer>, callback: ApiCallback<List<Customer>>) {
        val request = Request.Builder()
            .url(url)
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }
            
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val responseBody = it.body?.string()
                        try {
                            val customerResponse = gson.fromJson(responseBody, CustomerResponse::class.java)
                            allCustomers.addAll(customerResponse.results)
                            
                            // Si hay una página siguiente, cargarla
                            if (customerResponse.next != null && customerResponse.next.isNotEmpty()) {
                                getAllCustomersRecursive(customerResponse.next, allCustomers, callback)
                            } else {
                                // No hay más páginas, devolver todos los clientes
                                callback.onSuccess(allCustomers.toList())
                            }
                        } catch (e: Exception) {
                            callback.onError("Error al procesar respuesta: ${e.message}")
                        }
                    } else {
                        val errorMsg = when (it.code) {
                            403 -> "Error 403: Acceso prohibido. El servidor puede requerir autenticación o permisos especiales. Por favor contacta al administrador del sistema."
                            404 -> "Error 404: Recurso no encontrado"
                            500 -> "Error 500: Error interno del servidor"
                            else -> "Error del servidor: ${it.code}"
                        }
                        callback.onError(errorMsg)
                    }
                }
            }
        })
    }
    
    // Crear nuevo cliente
    fun createCustomer(customer: NewCustomer, callback: ApiCallback<Customer>) {
        val json = gson.toJson(customer)
        val requestBody = json.toRequestBody("application/json".toMediaType())
        
        val request = Request.Builder()
            .url(CUSTOMERS_ENDPOINT)
            .post(requestBody)
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }
            
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val responseBody = it.body?.string()
                        try {
                            val newCustomer = gson.fromJson(responseBody, Customer::class.java)
                            callback.onSuccess(newCustomer)
                        } catch (e: Exception) {
                            callback.onError("Error al procesar respuesta: ${e.message}")
                        }
                    } else {
                        callback.onError("Error del servidor: ${it.code}")
                    }
                }
            }
        })
    }
    
    // Actualizar cliente existente
    fun updateCustomer(customerId: Int, customer: NewCustomer, callback: ApiCallback<Customer>) {
        val json = gson.toJson(customer)
        val requestBody = json.toRequestBody("application/json".toMediaType())
        
        // Probar diferentes formatos de URL
        val updateUrl = "$CUSTOMERS_ENDPOINT$customerId/"
        println("DEBUG: URL de actualización: $updateUrl")
        
        val request = Request.Builder()
            .url(updateUrl)
            .put(requestBody)
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }
            
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    println("DEBUG: Response code: ${it.code}")
                    println("DEBUG: Response message: ${it.message}")
                    
                    if (it.isSuccessful) {
                        val responseBody = it.body?.string()
                        try {
                            val updatedCustomer = gson.fromJson(responseBody, Customer::class.java)
                            callback.onSuccess(updatedCustomer)
                        } catch (e: Exception) {
                            callback.onError("Error al procesar respuesta: ${e.message}")
                        }
                    } else {
                        val errorBody = it.body?.string()
                        println("DEBUG: Error response body: $errorBody")
                        callback.onError("Error del servidor: ${it.code} - ${it.message}")
                    }
                }
            }
        })
    }
    
    // Actualizar cliente existente - Versión alternativa con PATCH
    fun updateCustomerPatch(customerId: Int, customer: NewCustomer, callback: ApiCallback<Customer>) {
        val json = gson.toJson(customer)
        val requestBody = json.toRequestBody("application/json".toMediaType())
        
        val updateUrl = "$CUSTOMERS_ENDPOINT$customerId/"
        println("DEBUG: URL de actualización PATCH: $updateUrl")
        
        val request = Request.Builder()
            .url(updateUrl)
            .patch(requestBody)
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }
            
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    println("DEBUG: PATCH Response code: ${it.code}")
                    println("DEBUG: PATCH Response message: ${it.message}")
                    
                    if (it.isSuccessful) {
                        val responseBody = it.body?.string()
                        try {
                            val updatedCustomer = gson.fromJson(responseBody, Customer::class.java)
                            callback.onSuccess(updatedCustomer)
                        } catch (e: Exception) {
                            callback.onError("Error al procesar respuesta: ${e.message}")
                        }
                    } else {
                        val errorBody = it.body?.string()
                        println("DEBUG: PATCH Error response body: $errorBody")
                        callback.onError("Error del servidor: ${it.code} - ${it.message}")
                    }
                }
            }
        })
    }
    
    // Eliminar cliente
    fun deleteCustomer(customerId: Int, callback: ApiCallback<Boolean>) {
        val request = Request.Builder()
            .url("$CUSTOMERS_ENDPOINT$customerId/")
            .delete()
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }
            
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        callback.onSuccess(true)
                    } else {
                        callback.onError("Error del servidor: ${it.code}")
                    }
                }
            }
        })
    }
}
