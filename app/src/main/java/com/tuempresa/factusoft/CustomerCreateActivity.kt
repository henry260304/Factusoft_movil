package com.tuempresa.factusoft

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import kotlinx.coroutines.*

class CustomerCreateActivity : ComponentActivity() {
    
    private val apiService = ApiService()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                CustomerCreateScreen(
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
fun CustomerCreateScreen(
    apiService: ApiService,
    onBackPressed: () -> Unit,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Cliente") },
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
            // Mensaje de éxito
            if (showSuccessMessage) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E8)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50)
                        )
                        Column {
                            Text(
                                text = "✅ ¡Éxito!",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                            Text(
                                text = "El cliente se registró correctamente",
                                fontSize = 14.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }
            
            // Formulario
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
                        
                        // Crear cliente
                        isLoading = true
                        val newCustomer = NewCustomer(
                            custName = name,
                            custLastName = lastname,
                            custPhone = phone,
                            custEmail = email,
                            custAddress = if (address.isEmpty()) null else address
                        )
                        
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                apiService.createCustomer(newCustomer, object : ApiService.ApiCallback<Customer> {
                                    override fun onSuccess(data: Customer) {
                                        isLoading = false
                                        showSuccessMessage = true
                                        Toast.makeText(context, "Cliente creado exitosamente", Toast.LENGTH_SHORT).show()
                                        
                                        // Limpiar formulario
                                        name = ""
                                        lastname = ""
                                        phone = ""
                                        email = ""
                                        address = ""
                                        
                                        // Auto-cerrar después de 2 segundos
                                        scope.launch {
                                            delay(2000)
                                            onSuccess()
                                        }
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
