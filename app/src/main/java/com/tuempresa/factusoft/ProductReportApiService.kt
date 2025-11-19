package com.tuempresa.factusoft

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException
import android.util.Log

/**
 * Servicio de API para Reporte de Productos
 * Endpoint: https://factusoft-backend-2025-cndzh3e6cxcvdnch.northcentralus-01.azurewebsites.net/Dashboard/CosmosDB-ListarProductos/Lista-Productos
 */
class ProductReportApiService {
    
    private val baseUrl = "https://factusoft-backend-2025-cndzh3e6cxcvdnch.northcentralus-01.azurewebsites.net/Dashboard/CosmosDB-ListarProductos/Lista-Productos"
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
     * Obtener todos los productos del reporte
     */
    fun getAllProducts(callback: ApiCallback<List<ProductReport>>) {
        Log.d("ProductReportApiService", "getAllProducts iniciado")
        Log.d("ProductReportApiService", "URL: $baseUrl")
        
        val request = Request.Builder()
            .url(baseUrl)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header("User-Agent", "FactuSoft-Android-App")
            .get()
            .build()
        
        Log.d("ProductReportApiService", "Request headers: ${request.headers}")
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ProductReportApiService", "onFailure: ${e.message}", e)
                Log.e("ProductReportApiService", "StackTrace: ${e.stackTraceToString()}")
                val errorMsg = when {
                    e.message?.contains("Unable to resolve host") == true -> 
                        "No se puede conectar al servidor. Verifica tu conexión a internet."
                    e.message?.contains("timeout") == true -> 
                        "Tiempo de espera agotado. El servidor no respondió."
                    e.message?.contains("SSL") == true || e.message?.contains("certificate") == true -> 
                        "Error de certificado SSL. Verifica la configuración de seguridad de red."
                    else -> 
                        "Error de conexión: ${e.message}"
                }
                callback.onError(errorMsg)
            }
            
            override fun onResponse(call: Call, response: Response) {
                Log.d("ProductReportApiService", "onResponse - Código: ${response.code}")
                Log.d("ProductReportApiService", "Response Headers: ${response.headers}")
                Log.d("ProductReportApiService", "Request URL: ${call.request().url}")
                
                response.use {
                    val responseBodyText = response.body?.string()
                    Log.d("ProductReportApiService", "Response Body (tamaño: ${responseBodyText?.length} chars)")
                    Log.d("ProductReportApiService", "Response Body (primeros 500 chars): ${responseBodyText?.take(500)}")
                    
                    if (!response.isSuccessful) {
                        Log.e("ProductReportApiService", "❌ Respuesta no exitosa: ${response.code} - ${response.message}")
                        Log.e("ProductReportApiService", "Error body completo: $responseBodyText")
                        
                        val errorMsg = when (response.code) {
                            404 -> "Endpoint no encontrado (404). URL: ${call.request().url}"
                            401 -> "No autorizado (401). Verifica las credenciales."
                            403 -> "Acceso prohibido (403)."
                            500 -> "Error interno del servidor (500): ${responseBodyText?.take(300)}"
                            else -> "Error ${response.code}: ${response.message}. Respuesta: ${responseBodyText?.take(300)}"
                        }
                        callback.onError(errorMsg)
                        return
                    }
                    
                    try {
                        if (responseBodyText.isNullOrBlank()) {
                            Log.e("ProductReportApiService", "Respuesta vacía del servidor")
                            callback.onError("El servidor devolvió una respuesta vacía")
                            return
                        }
                        
                        Log.d("ProductReportApiService", "✅ Response exitosa (200 OK)")
                        
                        val listType = object : TypeToken<List<ProductReport>>() {}.type
                        val products = gson.fromJson<List<ProductReport>>(responseBodyText, listType)
                        
                        Log.d("ProductReportApiService", "✅ Productos procesados exitosamente: ${products.size}")
                        callback.onSuccess(products)
                    } catch (e: Exception) {
                        Log.e("ProductReportApiService", "Error al procesar JSON: ${e.message}", e)
                        Log.e("ProductReportApiService", "StackTrace: ${e.stackTraceToString()}")
                        Log.e("ProductReportApiService", "JSON completo que falló: $responseBodyText")
                        callback.onError("Error al procesar datos: ${e.message}")
                    }
                }
            }
        })
    }
}

