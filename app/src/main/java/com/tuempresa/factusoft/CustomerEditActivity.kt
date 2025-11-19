package com.tuempresa.factusoft

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*

class CustomerEditActivity : ComponentActivity() {
    
    private val apiService = ApiService()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val customer = intent.getSerializableExtra("customer") as? Customer
        
        setContent {
            MaterialTheme {
                CustomerEditScreen(
                    customer = customer,
                    apiService = apiService,
                    onBackPressed = { finish() },
                    onSuccess = {
                        setResult(RESULT_OK)
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerEditScreen(
    customer: Customer?,
    apiService: ApiService,
    onBackPressed: () -> Unit,
    onSuccess: () -> Unit
) {
    if (customer == null) {
        LaunchedEffect(Unit) {
            onBackPressed()
        }
        return
    }
    
    var name by remember { mutableStateOf(customer.custName) }
    var lastname by remember { mutableStateOf(customer.custLastName) }
    var phone by remember { mutableStateOf(customer.custPhone) }
    var email by remember { mutableStateOf(customer.custEmail) }
    var address by remember { mutableStateOf(customer.custAddress ?: "") }
    var isLoading by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Cliente") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            
            OutlinedTextField(
                value = lastname,
                onValueChange = { lastname = it },
                label = { Text("Apellido") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Dirección (Opcional)") },
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                minLines = 2
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onBackPressed,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Cancelar")
                }
                
                Button(
                    onClick = {
                        // Validación
                        if (name.isEmpty() || lastname.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                            Toast.makeText(context, "Por favor completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Toast.makeText(context, "Por favor ingresa un email válido", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        // Actualizar cliente en la API
                        isLoading = true
                        val updatedCustomer = NewCustomer(
                            custName = name,
                            custLastName = lastname,
                            custPhone = phone,
                            custEmail = email,
                            custAddress = if (address.isEmpty()) null else address
                        )
                        
                        apiService.updateCustomer(customer.idCustomer, updatedCustomer, object : ApiService.ApiCallback<Customer> {
                            override fun onSuccess(data: Customer) {
                                scope.launch(Dispatchers.Main) {
                                    isLoading = false
                                    Toast.makeText(context, "✅ Cliente actualizado exitosamente", Toast.LENGTH_SHORT).show()
                                    onSuccess()
                                }
                            }
                            
                            override fun onError(error: String) {
                                // Intentar con PATCH si PUT falla
                                if (error.contains("404") || error.contains("405")) {
                                    apiService.updateCustomerPatch(customer.idCustomer, updatedCustomer, object : ApiService.ApiCallback<Customer> {
                                        override fun onSuccess(data: Customer) {
                                            scope.launch(Dispatchers.Main) {
                                                isLoading = false
                                                Toast.makeText(context, "✅ Cliente actualizado exitosamente", Toast.LENGTH_SHORT).show()
                                                onSuccess()
                                            }
                                        }
                                        
                                        override fun onError(error: String) {
                                            scope.launch(Dispatchers.Main) {
                                                isLoading = false
                                                Toast.makeText(context, "❌ Error al actualizar: $error", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    })
                                } else {
                                    scope.launch(Dispatchers.Main) {
                                        isLoading = false
                                        Toast.makeText(context, "❌ Error al actualizar: $error", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        })
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}
