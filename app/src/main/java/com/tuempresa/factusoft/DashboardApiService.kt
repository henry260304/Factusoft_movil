package com.tuempresa.factusoft

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException
import android.util.Log

/**
 * Servicio de API para Dashboard
 */
class DashboardApiService {
    
    private val baseUrl = "https://factusoft-backend-2025-cndzh3e6cxcvdnch.northcentralus-01.azurewebsites.net"
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
     * Obtener ventas por categoría
     */
    fun getCategorySales(callback: ApiCallback<List<CategorySales>>) {
        val url = "$baseUrl/Dashboard/CategoriaVentaTotal-2025/Ventas-Categoria"
        Log.d("DashboardApiService", "getCategorySales - URL: $url")
        
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header("User-Agent", "FactuSoft-Android-App")
            .get()
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("DashboardApiService", "getCategorySales - Error: ${e.message}", e)
                val errorMsg = when {
                    e.message?.contains("Unable to resolve host") == true -> 
                        "No se puede conectar al servidor. Verifica tu conexión a internet."
                    e.message?.contains("timeout") == true -> 
                        "Tiempo de espera agotado."
                    else -> "Error de conexión: ${e.message}"
                }
                callback.onError(errorMsg)
            }
            
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBodyText = response.body?.string()
                    
                    if (!response.isSuccessful) {
                        Log.e("DashboardApiService", "getCategorySales - Error ${response.code}: $responseBodyText")
                        callback.onError("Error ${response.code}: ${response.message}")
                        return
                    }
                    
                    try {
                        val listType = object : TypeToken<List<CategorySales>>() {}.type
                        val data = gson.fromJson<List<CategorySales>>(responseBodyText, listType)
                        Log.d("DashboardApiService", "getCategorySales - Success: ${data.size} categorías")
                        callback.onSuccess(data)
                    } catch (e: Exception) {
                        Log.e("DashboardApiService", "getCategorySales - Error parsing: ${e.message}", e)
                        callback.onError("Error al procesar datos: ${e.message}")
                    }
                }
            }
        })
    }
    
    /**
     * Obtener Top 5 Clientes
     */
    fun getTopClients(callback: ApiCallback<List<TopClient>>) {
        val url = "$baseUrl/Dashboard/TopClientes-General/Top-5-ClientesGastoTotal"
        Log.d("DashboardApiService", "getTopClients - URL: $url")
        
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header("User-Agent", "FactuSoft-Android-App")
            .get()
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("DashboardApiService", "getTopClients - Error: ${e.message}", e)
                val errorMsg = when {
                    e.message?.contains("Unable to resolve host") == true -> 
                        "No se puede conectar al servidor. Verifica tu conexión a internet."
                    e.message?.contains("timeout") == true -> 
                        "Tiempo de espera agotado."
                    else -> "Error de conexión: ${e.message}"
                }
                callback.onError(errorMsg)
            }
            
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBodyText = response.body?.string()
                    
                    if (!response.isSuccessful) {
                        Log.e("DashboardApiService", "getTopClients - Error ${response.code}: $responseBodyText")
                        callback.onError("Error ${response.code}: ${response.message}")
                        return
                    }
                    
                    try {
                        val listType = object : TypeToken<List<TopClient>>() {}.type
                        val data = gson.fromJson<List<TopClient>>(responseBodyText, listType)
                        Log.d("DashboardApiService", "getTopClients - Success: ${data.size} clientes")
                        callback.onSuccess(data)
                    } catch (e: Exception) {
                        Log.e("DashboardApiService", "getTopClients - Error parsing: ${e.message}", e)
                        callback.onError("Error al procesar datos: ${e.message}")
                    }
                }
            }
        })
    }
    
    /**
     * Obtener ventas mensuales
     */
    fun getMonthlySales(callback: ApiCallback<List<MonthlySales>>) {
        val url = "$baseUrl/Dashboard/VentasMensual-2025/Cronología-VentasPorMes-2025"
        Log.d("DashboardApiService", "getMonthlySales - URL: $url")
        
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header("User-Agent", "FactuSoft-Android-App")
            .get()
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("DashboardApiService", "getMonthlySales - Error: ${e.message}", e)
                val errorMsg = when {
                    e.message?.contains("Unable to resolve host") == true -> 
                        "No se puede conectar al servidor. Verifica tu conexión a internet."
                    e.message?.contains("timeout") == true -> 
                        "Tiempo de espera agotado."
                    else -> "Error de conexión: ${e.message}"
                }
                callback.onError(errorMsg)
            }
            
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBodyText = response.body?.string()
                    
                    if (!response.isSuccessful) {
                        Log.e("DashboardApiService", "getMonthlySales - Error ${response.code}: $responseBodyText")
                        callback.onError("Error ${response.code}: ${response.message}")
                        return
                    }
                    
                    try {
                        val listType = object : TypeToken<List<MonthlySales>>() {}.type
                        val data = gson.fromJson<List<MonthlySales>>(responseBodyText, listType)
                        Log.d("DashboardApiService", "getMonthlySales - Success: ${data.size} meses")
                        callback.onSuccess(data)
                    } catch (e: Exception) {
                        Log.e("DashboardApiService", "getMonthlySales - Error parsing: ${e.message}", e)
                        callback.onError("Error al procesar datos: ${e.message}")
                    }
                }
            }
        })
    }
}

