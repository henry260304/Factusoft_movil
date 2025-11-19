package com.tuempresa.factusoft

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Modelo de Categoría de Producto
 */
data class Category(
    @SerializedName("idCategory")
    val idCategory: Int,
    
    @SerializedName("nameCategory")
    val nameCategory: String
) : Serializable

/**
 * Respuesta de la API de Categorías
 */
data class CategoryResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Category>
)


