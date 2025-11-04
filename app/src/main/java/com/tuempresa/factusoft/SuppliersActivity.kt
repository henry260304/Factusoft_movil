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
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                SuppliersScreen(
                    onBackPressed = { finish() },
                    apiService = apiService,
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
    
    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuppliersScreen(
    onBackPressed: () -> Unit,
    apiService: SupplierApiService,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Supplier) -> Unit
) {
    var suppliers by remember { mutableStateOf<List<Supplier>>(emptyList()) }
    var filteredSuppliers by remember { mutableStateOf<List<Supplier>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var supplierToDelete by remember { mutableStateOf<Supplier?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Cargar proveedores al iniciar
    LaunchedEffect(Unit) {
        isLoading = true
        withContext(Dispatchers.IO) {
            apiService.getAllSuppliers(object : SupplierApiService.ApiCallback<List<Supplier>> {
                override fun onSuccess(data: List<Supplier>) {
                    suppliers = data
                    filteredSuppliers = data
                    isLoading = false
                    Toast.makeText(context, "✅ ${data.size} proveedores cargados", Toast.LENGTH_SHORT).show()
                }
                
                override fun onError(error: String) {
                    isLoading = false
                    if (error.contains("403")) {
                        Toast.makeText(context, "⚠️ Servidor detenido. Cargando datos de prueba...", Toast.LENGTH_LONG).show()
                        loadTestSuppliers { testSuppliers ->
                            suppliers = testSuppliers
                            filteredSuppliers = testSuppliers
                        }
                    } else {
                        Toast.makeText(context, "❌ Error: $error\nCargando datos de prueba...", Toast.LENGTH_LONG).show()
                        loadTestSuppliers { testSuppliers ->
                            suppliers = testSuppliers
                            filteredSuppliers = testSuppliers
                        }
                    }
                }
            })
        }
    }
    
    // Filtrar proveedores cuando cambia la búsqueda
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
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
                    StatsItem("Total", suppliers.size.toString(), Color(0xFF2196F3))
                    StatsItem("Activos", suppliers.size.toString(), Color(0xFF4CAF50))
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
            
            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Lista de proveedores
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
                }
            }
        }
    }
    
    // Diálogo de confirmación de eliminación
    if (showDeleteDialog && supplierToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Proveedor") },
            text = { Text("¿Estás seguro de que quieres eliminar a ${supplierToDelete!!.nameSupplier}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            withContext(Dispatchers.IO) {
                                apiService.deleteSupplier(supplierToDelete!!.idSupplier, object : SupplierApiService.ApiCallback<Boolean> {
                                    override fun onSuccess(data: Boolean) {
                                        Toast.makeText(context, "✅ Proveedor eliminado exitosamente", Toast.LENGTH_SHORT).show()
                                        // Recargar lista
                                        apiService.getAllSuppliers(object : SupplierApiService.ApiCallback<List<Supplier>> {
                                            override fun onSuccess(data: List<Supplier>) {
                                                suppliers = data
                                                filteredSuppliers = data
                                                isLoading = false
                                            }
                                            override fun onError(error: String) {
                                                isLoading = false
                                            }
                                        })
                                    }
                                    
                                    override fun onError(error: String) {
                                        isLoading = false
                                        Toast.makeText(context, "❌ Error al eliminar: $error", Toast.LENGTH_LONG).show()
                                    }
                                })
                            }
                        }
                        showDeleteDialog = false
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Contacto",
                            tint = Color(0xFF666666),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = supplier.contact,
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = "Teléfono",
                            tint = Color(0xFF666666),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = supplier.PhoneNumber,
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "Email",
                            tint = Color(0xFF666666),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = supplier.email,
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color(0xFF2196F3)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color(0xFFF44336)
                        )
                    }
                }
            }
        }
    }
}

private fun loadTestSuppliers(onLoaded: (List<Supplier>) -> Unit) {
    val testSuppliers = listOf(
        Supplier(1, "Proveedor Tecnológico S.A.", "Juan Pérez", "+1234567890", "juan@techprov.com"),
        Supplier(2, "Distribuidora Global", "María García", "+1234567891", "maria@distglobal.com"),
        Supplier(3, "Suministros Industriales", "Carlos López", "+1234567892", "carlos@sumindustriales.com"),
        Supplier(4, "Materiales Premium", "Ana Martínez", "+1234567893", "ana@matpremium.com"),
        Supplier(5, "Equipos y Herramientas", "Luis Rodríguez", "+1234567894", "luis@equiposyherr.com"),
        Supplier(6, "Insumos Especializados", "Carmen Silva", "+1234567895", "carmen@insumosesp.com"),
        Supplier(7, "Productos Químicos Ltd", "Roberto Torres", "+1234567896", "roberto@prodquimicos.com"),
        Supplier(8, "Maquinaria Industrial", "Elena Vargas", "+1234567897", "elena@maqindustrial.com"),
        Supplier(9, "Componentes Electrónicos", "Diego Morales", "+1234567898", "diego@compelectronicos.com"),
        Supplier(10, "Servicios Logísticos", "Patricia Ruiz", "+1234567899", "patricia@servlogisticos.com")
    )
    onLoaded(testSuppliers)
}
