package com.tuempresa.factusoft

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Modelo de Producto - Conectado a API Real
 */
data class Product(
    @SerializedName("idProduct")
    val idProduct: Int,
    
    @SerializedName("nameProduct")
    val nameProduct: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("price_cost")
    val priceCost: String,
    
    @SerializedName("price_selling")
    val priceSelling: String,
    
    @SerializedName("current_stock")
    val currentStock: Int,
    
    @SerializedName("min_stock")
    val minStock: Int,
    
    @SerializedName("category")
    val category: ProductCategory?,
    
    @SerializedName("supplier")
    val supplier: ProductSupplier?
) : Serializable

/**
 * Categoría de producto (objeto anidado en la respuesta)
 */
data class ProductCategory(
    @SerializedName("nameCategory")
    val nameCategory: String
) : Serializable

/**
 * Proveedor de producto (objeto anidado en la respuesta)
 */
data class ProductSupplier(
    @SerializedName("nameSupplier")
    val nameSupplier: String
) : Serializable

/**
 * Respuesta paginada de la API de Productos
 */
data class ProductResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Product>
)

/**
 * Modelo para crear un nuevo producto
 */
data class NewProduct(
    @SerializedName("nameProduct")
    val nameProduct: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("price_cost")
    val priceCost: String,
    
    @SerializedName("price_selling")
    val priceSelling: String,
    
    @SerializedName("current_stock")
    val currentStock: Int,
    
    @SerializedName("min_stock")
    val minStock: Int,
    
    @SerializedName("categoryfk")
    val categoryId: Int, // ID de categoría - OBLIGATORIO
    
    @SerializedName("supplierfk")
    val supplierId: Int // ID de proveedor - OBLIGATORIO
) : Serializable

