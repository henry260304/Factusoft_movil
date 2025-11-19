package com.tuempresa.factusoft

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Modelo para Ventas por Categoría
 * Endpoint: /Dashboard/CategoriaVentaTotal-2025/Ventas-Categoria
 */
data class CategorySales(
    @SerializedName("Categoria")
    val categoria: String,
    
    @SerializedName("TotalLinea")
    val totalLinea: Double
) : Serializable

/**
 * Modelo para Top 5 Clientes
 * Endpoint: /Dashboard/TopClientes-General/Top-5-ClientesGastoTotal
 */
data class TopClient(
    @SerializedName("Nombre")
    val nombre: String,
    
    @SerializedName("TotalGasto")
    val totalGasto: Double
) : Serializable

/**
 * Modelo para Ventas Mensuales
 * Endpoint: /Dashboard/VentasMensual-2025/Cronología-VentasPorMes-2025
 */
data class MonthlySales(
    @SerializedName("Anio")
    val anio: Int,
    
    @SerializedName("NombreMes")
    val nombreMes: String,
    
    @SerializedName("TotalVentas")
    val totalVentas: Double
) : Serializable

