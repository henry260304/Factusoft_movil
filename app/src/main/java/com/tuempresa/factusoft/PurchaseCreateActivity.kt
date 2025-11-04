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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PurchaseCreateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                PurchaseCreateScreen(
                    onBackPressed = { finish() },
                    onSuccess = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseCreateScreen(
    onBackPressed: () -> Unit,
    onSuccess: () -> Unit
) {
    var supplier by remember { mutableStateOf("") }
    var product by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unitPrice by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Compra") },
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
                value = supplier,
                onValueChange = { supplier = it },
                label = { Text("Proveedor") },
                leadingIcon = {
                    Icon(Icons.Default.Business, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            
            OutlinedTextField(
                value = product,
                onValueChange = { product = it },
                label = { Text("Producto") },
                leadingIcon = {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Cantidad") },
                leadingIcon = {
                    Icon(Icons.Default.Numbers, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            
            OutlinedTextField(
                value = unitPrice,
                onValueChange = { unitPrice = it },
                label = { Text("Precio Unitario") },
                leadingIcon = {
                    Icon(Icons.Default.AttachMoney, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            
            // Mostrar total calculado
            val total = try {
                val q = quantity.toDoubleOrNull() ?: 0.0
                val p = unitPrice.toDoubleOrNull() ?: 0.0
                q * p
            } catch (e: Exception) {
                0.0
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total:")
                    Text(
                        text = "$${"%.2f".format(total)}",
                        color = Color(0xFFFF9800),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
            
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas (Opcional)") },
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                minLines = 3
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
                        if (supplier.isEmpty() || product.isEmpty() || quantity.isEmpty() || unitPrice.isEmpty()) {
                            Toast.makeText(context, "Por favor completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        isLoading = true
                        scope.launch {
                            delay(1000)
                            Toast.makeText(context, "✅ Compra registrada exitosamente", Toast.LENGTH_SHORT).show()
                            onSuccess()
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
