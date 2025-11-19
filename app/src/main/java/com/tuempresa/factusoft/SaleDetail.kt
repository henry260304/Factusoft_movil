package com.tuempresa.factusoft

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Modelo de Detalle de Venta
 * Endpoint: /Dashboard/CosmosDB-DetalleVenta/Detalle/{id}/
 */
data class SaleDetail(
    @SerializedName("factura_id")
    val facturaId: Int,
    
    @SerializedName("fecha")
    val fecha: String,
    
    @SerializedName("total")
    val total: Double,
    
    @SerializedName("cliente")
    val cliente: List<ClienteInfo>,
    
    @SerializedName("vendedor")
    val vendedor: List<VendedorInfo>,
    
    @SerializedName("productos_vendidos")
    val productosVendidos: List<ProductoVendido>
) : Serializable

data class ClienteInfo(
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("apellido")
    val apellido: String
) : Serializable

data class VendedorInfo(
    @SerializedName("nombre_usuario")
    val nombreUsuario: String,
    
    @SerializedName("nombre_completo")
    val nombreCompleto: String
) : Serializable

data class ProductoVendido(
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("cantidad")
    val cantidad: Int,
    
    @SerializedName("precio_unitario")
    val precioUnitario: Double
) : Serializable
