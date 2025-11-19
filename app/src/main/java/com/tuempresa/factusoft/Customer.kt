package com.tuempresa.factusoft

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Modelo de Cliente - Conectado a API Real
 */
data class Customer(
    @SerializedName("idCustomer")
    val idCustomer: Int,
    
    @SerializedName("CustName")
    val custName: String,
    
    @SerializedName("CustLastName")
    val custLastName: String,
    
    @SerializedName("Cust_phone")
    val custPhone: String,
    
    @SerializedName("Cust_email")
    val custEmail: String,
    
    @SerializedName("CustAddress")
    val custAddress: String?
) : Serializable

/**
 * Respuesta paginada de la API
 */
data class CustomerResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Customer>
)

/**
 * Modelo para crear/actualizar un cliente
 */
data class NewCustomer(
    @SerializedName("CustName")
    val custName: String,
    
    @SerializedName("CustLastName")
    val custLastName: String,
    
    @SerializedName("Cust_phone")
    val custPhone: String,
    
    @SerializedName("Cust_email")
    val custEmail: String,
    
    @SerializedName("CustAddress")
    val custAddress: String?
) : Serializable
