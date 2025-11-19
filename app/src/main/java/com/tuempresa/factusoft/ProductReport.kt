package com.tuempresa.factusoft

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Modelo de Producto para Reporte
 * Endpoint: /Dashboard/CosmosDB-ListarProductos/Lista-Productos
 */
data class ProductReport(
    @SerializedName("nombre_producto")
    val nombreProducto: String,
    
    @SerializedName("precio_venta")
    val precioVenta: Double,
    
    @SerializedName("stock")
    val stock: Int,
    
    @SerializedName("categoria")
    val categoria: String,
    
    @SerializedName("proveedor")
    val proveedor: String
) : Serializable

