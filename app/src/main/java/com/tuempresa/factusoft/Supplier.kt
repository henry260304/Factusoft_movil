package com.tuempresa.factusoft

import java.io.Serializable

data class Supplier(
    val idSupplier: Int,
    val nameSupplier: String,
    val contact: String,
    val PhoneNumber: String,
    val email: String
) : Serializable
