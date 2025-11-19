package com.tuempresa.factusoft

import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import android.util.Log

/**
 * Servicio de API para Detalles de Ventas
 * Endpoint: https://factusoft-backend-2025-cndzh3e6cxcvdnch.northcentralus-01.azurewebsites.net/Dashboard/CosmosDB-DetalleVenta/Detalle/{id}/
 */
class SaleDetailApiService {
    
    private val baseUrl = "https://factusoft-backend-2025-cndzh3e6cxcvdnch.northcentralus-01.azurewebsites.net/Dashboard/CosmosDB-DetalleVenta/Detalle"
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
     * Obtener detalles de venta por ID
     * @param saleId ID de la venta/factura
     */
    fun getSaleDetailById(saleId: Int, callback: ApiCallback<SaleDetail>) {
        val url = "$baseUrl/$saleId/"
        Log.d("SaleDetailApiService", "getSaleDetailById iniciado")
        Log.d("SaleDetailApiService", "URL: $url")
        Log.d("SaleDetailApiService", "Sale ID: $saleId")
        
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header("User-Agent", "FactuSoft-Android-App")
            .get()
            .build()
        
        Log.d("SaleDetailApiService", "Request headers: ${request.headers}")
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SaleDetailApiService", "onFailure: ${e.message}", e)
                Log.e("SaleDetailApiService", "StackTrace: ${e.stackTraceToString()}")
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
                Log.d("SaleDetailApiService", "onResponse - Código: ${response.code}")
                Log.d("SaleDetailApiService", "Response Headers: ${response.headers}")
                Log.d("SaleDetailApiService", "Request URL: ${call.request().url}")
                Log.d("SaleDetailApiService", "Request Method: ${call.request().method}")
                
                response.use {
                    val responseBodyText = response.body?.string()
                    Log.d("SaleDetailApiService", "Response Body (tamaño: ${responseBodyText?.length} chars)")
                    Log.d("SaleDetailApiService", "Response Body completo: $responseBodyText")
                    
                    if (!response.isSuccessful) {
                        Log.e("SaleDetailApiService", "❌ Respuesta no exitosa: ${response.code} - ${response.message}")
                        Log.e("SaleDetailApiService", "Error body completo: $responseBodyText")
                        
                        val errorMsg = when (response.code) {
                            404 -> "Venta no encontrada (404). Verifica que el ID de venta sea correcto."
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
                            Log.e("SaleDetailApiService", "Respuesta vacía del servidor")
                            callback.onError("El servidor devolvió una respuesta vacía")
                            return
                        }
                        
                        Log.d("SaleDetailApiService", "✅ Response exitosa (200 OK)")
                        Log.d("SaleDetailApiService", "JSON (primeros 2000 chars): ${responseBodyText.take(2000)}")
                        
                        val saleDetail = gson.fromJson(responseBodyText, SaleDetail::class.java)
                        Log.d("SaleDetailApiService", "✅ Detalle de venta parseado exitosamente")
                        Log.d("SaleDetailApiService", "Factura ID: ${saleDetail.facturaId}")
                        Log.d("SaleDetailApiService", "Total: ${saleDetail.total}")
                        Log.d("SaleDetailApiService", "Productos: ${saleDetail.productosVendidos.size}")
                        
                        callback.onSuccess(saleDetail)
                    } catch (e: Exception) {
                        Log.e("SaleDetailApiService", "Error al procesar JSON: ${e.message}", e)
                        Log.e("SaleDetailApiService", "StackTrace: ${e.stackTraceToString()}")
                        Log.e("SaleDetailApiService", "JSON completo que falló: $responseBodyText")
                        callback.onError("Error al procesar datos: ${e.message}")
                    }
                }
            }
        })
    }
}
