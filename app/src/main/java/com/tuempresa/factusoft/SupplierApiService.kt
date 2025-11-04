package com.tuempresa.factusoft

import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class SupplierApiService {
    
    companion object {
        private const val BASE_URL = "https://factusoft-backend2025.azurewebsites.net/Catalogos/Supplier/"
        private const val TAG = "SupplierApiService"
    }
    
    interface ApiCallback<T> {
        fun onSuccess(data: T)
        fun onError(error: String)
    }
    
    // Obtener TODOS los proveedores (todas las páginas)
    fun getAllSuppliers(callback: ApiCallback<List<Supplier>>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val allSuppliers = mutableListOf<Supplier>()
                var nextUrl = BASE_URL
                var pageNumber = 1
                
                Log.d(TAG, "🔄 Iniciando carga de TODOS los proveedores de la base de datos...")
                
                // Cargar todas las páginas
                while (nextUrl.isNotEmpty()) {
                    Log.d(TAG, "📄 Cargando página $pageNumber: $nextUrl")
                    
                    val url = URL(nextUrl)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.setRequestProperty("Accept", "application/json")
                    connection.setRequestProperty("User-Agent", "FactuSoft-Android")
                    
                    Log.d(TAG, "=== REQUEST DEBUG ===")
                    Log.d(TAG, "URL: $nextUrl")
                    Log.d(TAG, "Method: GET")
                    
                    val responseCode = connection.responseCode
                    Log.d(TAG, "=== RESPONSE DEBUG ===")
                    Log.d(TAG, "Response code para página $pageNumber: $responseCode")
                    
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val inputStream = connection.inputStream
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val response = StringBuilder()
                        var line: String?
                        
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }
                        reader.close()
                        inputStream.close()
                        
                        val jsonResponse = response.toString()
                        Log.d(TAG, "Respuesta página $pageNumber: ${jsonResponse.take(200)}...")
                        
                        val pageSuppliers = parseSuppliersResponse(jsonResponse)
                        allSuppliers.addAll(pageSuppliers)
                        
                        // Obtener URL de la siguiente página
                        nextUrl = getNextPageUrl(jsonResponse)
                        pageNumber++
                        
                        Log.d(TAG, "✅ Página $pageNumber completada. Proveedores cargados: ${pageSuppliers.size}")
                        Log.d(TAG, "📊 Total acumulado: ${allSuppliers.size} proveedores")
                        
                        if (nextUrl.isNotEmpty()) {
                            Log.d(TAG, "➡️ Siguiente página: $nextUrl")
                        } else {
                            Log.d(TAG, "🏁 No hay más páginas. Carga completa.")
                        }
                        
                    } else {
                        val errorStream = connection.errorStream
                        val errorReader = BufferedReader(InputStreamReader(errorStream))
                        val errorResponse = StringBuilder()
                        var line: String?
                        
                        while (errorReader.readLine().also { line = it } != null) {
                            errorResponse.append(line)
                        }
                        errorReader.close()
                        errorStream.close()
                        
                        val errorMsg = when (responseCode) {
                            403 -> "Error 403: Servidor detenido o acceso prohibido"
                            404 -> "Error 404: Recurso no encontrado"
                            500 -> "Error 500: Error interno del servidor"
                            else -> "Error $responseCode: ${errorResponse.toString()}"
                        }
                        
                        withContext(Dispatchers.Main) {
                            callback.onError(errorMsg)
                        }
                        return@launch
                    }
                    
                    connection.disconnect()
                }
                
                Log.d(TAG, "🎉 CARGA COMPLETA: ${allSuppliers.size} proveedores de TODA la base de datos")
                Log.d(TAG, "📋 Lista completa: ${allSuppliers.map { "${it.idSupplier}: ${it.nameSupplier}" }}")
                
                withContext(Dispatchers.Main) {
                    callback.onSuccess(allSuppliers)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error getting all suppliers", e)
                withContext(Dispatchers.Main) {
                    callback.onError("Error de conexión: ${e.message}")
                }
            }
        }
    }
    
    // Crear nuevo proveedor
    fun createSupplier(supplier: Supplier, callback: ApiCallback<Supplier>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(BASE_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("User-Agent", "FactuSoft-Android")
                connection.doOutput = true
                
                val jsonObject = JSONObject().apply {
                    put("nameSupplier", supplier.nameSupplier)
                    put("contact", supplier.contact)
                    put("PhoneNumber", supplier.PhoneNumber)
                    put("email", supplier.email)
                }
                
                val outputStream = connection.outputStream
                val writer = OutputStreamWriter(outputStream)
                writer.write(jsonObject.toString())
                writer.flush()
                writer.close()
                outputStream.close()
                
                val responseCode = connection.responseCode
                Log.d(TAG, "Create supplier response code: $responseCode")
                
                if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = StringBuilder()
                    var line: String?
                    
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()
                    inputStream.close()
                    
                    val createdSupplier = parseSupplierResponse(response.toString())
                    
                    withContext(Dispatchers.Main) {
                        callback.onSuccess(createdSupplier)
                    }
                } else {
                    val errorStream = connection.errorStream
                    val errorReader = BufferedReader(InputStreamReader(errorStream))
                    val errorResponse = StringBuilder()
                    var line: String?
                    
                    while (errorReader.readLine().also { line = it } != null) {
                        errorResponse.append(line)
                    }
                    errorReader.close()
                    errorStream.close()
                    
                    withContext(Dispatchers.Main) {
                        callback.onError("Error del servidor ($responseCode): ${errorResponse.toString()}")
                    }
                }
                
                connection.disconnect()
                
            } catch (e: Exception) {
                Log.e(TAG, "Error creating supplier", e)
                withContext(Dispatchers.Main) {
                    callback.onError("Error de conexión: ${e.message}")
                }
            }
        }
    }
    
    // Actualizar proveedor
    fun updateSupplier(supplier: Supplier, callback: ApiCallback<Supplier>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("$BASE_URL${supplier.idSupplier}/")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "PUT"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("User-Agent", "FactuSoft-Android")
                connection.doOutput = true
                
                val jsonObject = JSONObject().apply {
                    put("idSupplier", supplier.idSupplier)
                    put("nameSupplier", supplier.nameSupplier)
                    put("contact", supplier.contact)
                    put("PhoneNumber", supplier.PhoneNumber)
                    put("email", supplier.email)
                }
                
                val outputStream = connection.outputStream
                val writer = OutputStreamWriter(outputStream)
                writer.write(jsonObject.toString())
                writer.flush()
                writer.close()
                outputStream.close()
                
                val responseCode = connection.responseCode
                Log.d(TAG, "Update supplier response code: $responseCode")
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = StringBuilder()
                    var line: String?
                    
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()
                    inputStream.close()
                    
                    val updatedSupplier = parseSupplierResponse(response.toString())
                    
                    withContext(Dispatchers.Main) {
                        callback.onSuccess(updatedSupplier)
                    }
                } else {
                    val errorStream = connection.errorStream
                    val errorReader = BufferedReader(InputStreamReader(errorStream))
                    val errorResponse = StringBuilder()
                    var line: String?
                    
                    while (errorReader.readLine().also { line = it } != null) {
                        errorResponse.append(line)
                    }
                    errorReader.close()
                    errorStream.close()
                    
                    withContext(Dispatchers.Main) {
                        callback.onError("Error del servidor ($responseCode): ${errorResponse.toString()}")
                    }
                }
                
                connection.disconnect()
                
            } catch (e: Exception) {
                Log.e(TAG, "Error updating supplier", e)
                withContext(Dispatchers.Main) {
                    callback.onError("Error de conexión: ${e.message}")
                }
            }
        }
    }
    
    // Eliminar proveedor
    fun deleteSupplier(supplierId: Int, callback: ApiCallback<Boolean>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("$BASE_URL$supplierId/")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "DELETE"
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("User-Agent", "FactuSoft-Android")
                
                val responseCode = connection.responseCode
                Log.d(TAG, "Delete supplier response code: $responseCode")
                
                if (responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_OK) {
                    withContext(Dispatchers.Main) {
                        callback.onSuccess(true)
                    }
                } else {
                    val errorStream = connection.errorStream
                    val errorReader = BufferedReader(InputStreamReader(errorStream))
                    val errorResponse = StringBuilder()
                    var line: String?
                    
                    while (errorReader.readLine().also { line = it } != null) {
                        errorResponse.append(line)
                    }
                    errorReader.close()
                    errorStream.close()
                    
                    withContext(Dispatchers.Main) {
                        callback.onError("Error del servidor ($responseCode): ${errorResponse.toString()}")
                    }
                }
                
                connection.disconnect()
                
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting supplier", e)
                withContext(Dispatchers.Main) {
                    callback.onError("Error de conexión: ${e.message}")
                }
            }
        }
    }
    
    // Parsear respuesta de lista de proveedores
    private fun parseSuppliersResponse(jsonResponse: String): List<Supplier> {
        val suppliers = mutableListOf<Supplier>()
        try {
            val jsonObject = JSONObject(jsonResponse)
            
            // Obtener información de paginación si existe
            val count = jsonObject.optInt("count", 0)
            val next = jsonObject.optString("next", null)
            val previous = jsonObject.optString("previous", null)
            
            Log.d(TAG, "Total proveedores en servidor: $count")
            Log.d(TAG, "Página siguiente: $next")
            Log.d(TAG, "Página anterior: $previous")
            
            val resultsArray = jsonObject.getJSONArray("results")
            Log.d(TAG, "Proveedores en esta página: ${resultsArray.length()}")
            
            for (i in 0 until resultsArray.length()) {
                val supplierJson = resultsArray.getJSONObject(i)
                val supplier = Supplier(
                    idSupplier = supplierJson.getInt("idSupplier"),
                    nameSupplier = supplierJson.getString("nameSupplier"),
                    contact = supplierJson.getString("contact"),
                    PhoneNumber = supplierJson.getString("PhoneNumber"),
                    email = supplierJson.getString("email")
                )
                suppliers.add(supplier)
            }
            
            Log.d(TAG, "Proveedores parseados exitosamente: ${suppliers.size}")
            
            // Si hay más páginas, informar al usuario
            if (!next.isNullOrEmpty()) {
                Log.w(TAG, "ATENCIÓN: Hay más proveedores en el servidor. Página siguiente: $next")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing suppliers response", e)
        }
        return suppliers
    }
    
    // Parsear respuesta de un proveedor
    private fun parseSupplierResponse(jsonResponse: String): Supplier {
        val jsonObject = JSONObject(jsonResponse)
        return Supplier(
            idSupplier = jsonObject.getInt("idSupplier"),
            nameSupplier = jsonObject.getString("nameSupplier"),
            contact = jsonObject.getString("contact"),
            PhoneNumber = jsonObject.getString("PhoneNumber"),
            email = jsonObject.getString("email")
        )
    }
    
    // Obtener URL de la siguiente página
    private fun getNextPageUrl(jsonResponse: String): String {
        return try {
            val jsonObject = JSONObject(jsonResponse)
            val nextUrl = jsonObject.optString("next", "")
            Log.d(TAG, "🔍 Next URL encontrada: $nextUrl")
            nextUrl
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing next URL", e)
            ""
        }
    }
}
