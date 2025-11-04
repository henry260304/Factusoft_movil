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

class SupplierEditActivity : ComponentActivity() {
    
    private val apiService = SupplierApiService()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val supplier = intent.getSerializableExtra("supplier") as? Supplier
        
        setContent {
            MaterialTheme {
                SupplierEditScreen(
                    supplier = supplier,
                    apiService = apiService,
                    onBackPressed = { finish() },
                    onSuccess = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierEditScreen(
    supplier: Supplier?,
    apiService: SupplierApiService,
    onBackPressed: () -> Unit,
    onSuccess: () -> Unit
) {
    if (supplier == null) {
        LaunchedEffect(Unit) {
            onBackPressed()
        }
        return
    }
    
    var name by remember { mutableStateOf(supplier.nameSupplier) }
    var contact by remember { mutableStateOf(supplier.contact) }
    var phone by remember { mutableStateOf(supplier.PhoneNumber) }
    var email by remember { mutableStateOf(supplier.email) }
    var isLoading by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Proveedor") },
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
                label = { Text("Nombre de la Empresa") },
                leadingIcon = {
                    Icon(Icons.Default.Business, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            
            OutlinedTextField(
                value = contact,
                onValueChange = { contact = it },
                label = { Text("Persona de Contacto") },
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
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
                        if (name.isEmpty() || contact.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                            Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Toast.makeText(context, "Por favor ingresa un email válido", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        isLoading = true
                        val updatedSupplier = Supplier(
                            idSupplier = supplier.idSupplier,
                            nameSupplier = name,
                            contact = contact,
                            PhoneNumber = phone,
                            email = email
                        )
                        
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                apiService.updateSupplier(updatedSupplier, object : SupplierApiService.ApiCallback<Supplier> {
                                    override fun onSuccess(data: Supplier) {
                                        isLoading = false
                                        Toast.makeText(context, "✅ Proveedor actualizado exitosamente", Toast.LENGTH_SHORT).show()
                                        onSuccess()
                                    }
                                    
                                    override fun onError(error: String) {
                                        isLoading = false
                                        Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                                    }
                                })
                            }
                        }
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
