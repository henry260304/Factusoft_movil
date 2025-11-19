package com.tuempresa.factusoft

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*

class SuppliersActivity : ComponentActivity() {
    
    private val apiService = SupplierApiService()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                SuppliersScreen(
                    apiService = apiService,
                    onBackPressed = { finish() },
                    onNavigateToCreate = {
                        startActivity(Intent(this, SupplierCreateActivity::class.java))
                    },
                    onNavigateToEdit = { supplier ->
                        val intent = Intent(this, SupplierEditActivity::class.java)
                        intent.putExtra("supplier", supplier)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuppliersScreen(
    apiService: SupplierApiService,
    onBackPressed: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Supplier) -> Unit
) {
    var suppliers by remember { mutableStateOf<List<Supplier>>(emptyList()) }
    var filteredSuppliers by remember { mutableStateOf<List<Supplier>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var supplierToDelete by remember { mutableStateOf<Supplier?>(null) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Función para cargar proveedores desde la API
    fun loadSuppliers() {
        hasError = false
        isLoading = true
        
        apiService.getAllSuppliers(object : SupplierApiService.ApiCallback<List<Supplier>> {
            override fun onSuccess(data: List<Supplier>) {
                scope.launch(Dispatchers.Main) {
                    suppliers = data
                    filteredSuppliers = data
                    isLoading = false
                    hasError = false
                    Toast.makeText(context, "✅ ${data.size} proveedores cargados del servidor", Toast.LENGTH_SHORT).show()
                }
            }
            
            override fun onError(error: String) {
                scope.launch(Dispatchers.Main) {
                    isLoading = false
                    hasError = true
                    errorMessage = error
                    suppliers = emptyList()
                    filteredSuppliers = emptyList()
                    Toast.makeText(context, "❌ Error del servidor: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
    
    // Cargar al iniciar
    LaunchedEffect(Unit) {
        loadSuppliers()
    }
    
    // Filtrar proveedores
    LaunchedEffect(searchQuery) {
        filteredSuppliers = if (searchQuery.isEmpty()) {
            suppliers
        } else {
            suppliers.filter { supplier ->
                supplier.nameSupplier.contains(searchQuery, ignoreCase = true) ||
                supplier.contact.contains(searchQuery, ignoreCase = true) ||
                supplier.PhoneNumber.contains(searchQuery, ignoreCase = true) ||
                supplier.email.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Proveedores") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { loadSuppliers() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Recargar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Proveedor", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Estadísticas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatsItem("Cargados", suppliers.size.toString(), Color(0xFF2196F3))
                    StatsItem("Filtrados", filteredSuppliers.size.toString(), Color(0xFF4CAF50))
                }
            }
            
            // Buscador
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar proveedores...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true
            )
            
            // Loading o lista
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando proveedores...")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSuppliers) { supplier ->
                        SupplierCard(
                            supplier = supplier,
                            onEdit = { onNavigateToEdit(supplier) },
                            onDelete = {
                                supplierToDelete = supplier
                                showDeleteDialog = true
                            }
                        )
                    }
                    
                    // Mensaje si está vacío
                    if (filteredSuppliers.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                colors = if (hasError) {
                                    CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                                } else {
                                    CardDefaults.cardColors()
                                }
                            ) {
                                Column(
                                    modifier = Modifier.padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        if (hasError) Icons.Default.CloudOff else Icons.Default.SearchOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = if (hasError) Color(0xFFF44336) else Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = if (hasError) {
                                            "Error al conectar con el servidor"
                                        } else if (searchQuery.isEmpty()) {
                                            "No hay proveedores disponibles"
                                        } else {
                                            "No se encontraron proveedores"
                                        },
                                        color = if (hasError) Color(0xFFC62828) else Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                    
                                    if (hasError) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = errorMessage,
                                            fontSize = 12.sp,
                                            color = Color(0xFF666666)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = { loadSuppliers() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF2196F3)
                                            )
                                        ) {
                                            Icon(Icons.Default.Refresh, contentDescription = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Reintentar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Diálogo de eliminación
    if (showDeleteDialog && supplierToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Proveedor") },
            text = { Text("¿Estás seguro de eliminar a ${supplierToDelete!!.nameSupplier}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        isLoading = true
                        apiService.deleteSupplier(supplierToDelete!!.idSupplier, object : SupplierApiService.ApiCallback<Boolean> {
                            override fun onSuccess(data: Boolean) {
                                scope.launch(Dispatchers.Main) {
                                    Toast.makeText(context, "✅ Proveedor eliminado", Toast.LENGTH_SHORT).show()
                                    loadSuppliers()
                                }
                            }
                            
                            override fun onError(error: String) {
                                scope.launch(Dispatchers.Main) {
                                    isLoading = false
                                    Toast.makeText(context, "❌ Error al eliminar: $error", Toast.LENGTH_LONG).show()
                                }
                            }
                        })
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun SupplierCard(
    supplier: Supplier,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = supplier.nameSupplier,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF666666), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = supplier.contact, fontSize = 14.sp, color = Color(0xFF666666))
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF666666), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = supplier.PhoneNumber, fontSize = 14.sp, color = Color(0xFF666666))
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF666666), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = supplier.email, fontSize = 14.sp, color = Color(0xFF666666))
                    }
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF2196F3))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFF44336))
                    }
                }
            }
        }
    }
}
