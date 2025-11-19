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
import androidx.compose.foundation.lazy.rememberLazyListState
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

class CustomersActivity : ComponentActivity() {
    
    private val apiService = ApiService()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                CustomersScreen(
                    apiService = apiService,
                    onBackPressed = { finish() },
                    onNavigateToCreate = {
                        startActivity(Intent(this, CustomerCreateActivity::class.java))
                    },
                    onNavigateToEdit = { customer ->
                        val intent = Intent(this, CustomerEditActivity::class.java)
                        intent.putExtra("customer", customer)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    apiService: ApiService,
    onBackPressed: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Customer) -> Unit
) {
    var customers by remember { mutableStateOf<List<Customer>>(emptyList()) }
    var filteredCustomers by remember { mutableStateOf<List<Customer>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(1) }
    var totalCount by remember { mutableStateOf(0) }
    var nextPageUrl by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    // Función para cargar la primera página
    fun loadFirstPage() {
        hasError = false
        isLoading = true
        currentPage = 1
        
        apiService.getAllCustomers(object : ApiService.ApiCallback<List<Customer>> {
            override fun onSuccess(data: List<Customer>) {
                scope.launch(Dispatchers.Main) {
                    customers = data
                    filteredCustomers = data
                    isLoading = false
                    hasError = false
                    Toast.makeText(context, "✅ ${data.size} clientes cargados", Toast.LENGTH_SHORT).show()
                }
            }
            
            override fun onError(error: String) {
                scope.launch(Dispatchers.Main) {
                    isLoading = false
                    hasError = true
                    errorMessage = error
                    Toast.makeText(context, "❌ Error: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
    
    // Función para cargar más clientes (scroll infinito)
    fun loadMoreCustomers() {
        if (nextPageUrl == null || isLoadingMore) return
        
        isLoadingMore = true
        // Implementar carga de página siguiente si es necesario
        // Por ahora solo carga la primera página
        isLoadingMore = false
    }
    
    // Cargar primera página al iniciar
    LaunchedEffect(Unit) {
        loadFirstPage()
    }
    
    // Filtrar
    LaunchedEffect(searchQuery) {
        filteredCustomers = if (searchQuery.isEmpty()) {
            customers
        } else {
            customers.filter { customer ->
                customer.custName.contains(searchQuery, ignoreCase = true) ||
                customer.custLastName.contains(searchQuery, ignoreCase = true) ||
                customer.custPhone.contains(searchQuery, ignoreCase = true) ||
                customer.custEmail.contains(searchQuery, ignoreCase = true) ||
                (customer.custAddress?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clientes") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { loadFirstPage() }) {
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
                Icon(Icons.Default.Add, contentDescription = "Agregar Cliente", tint = Color.White)
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
                    StatsItem("Cargados", customers.size.toString(), Color(0xFF2196F3))
                    StatsItem("Filtrados", filteredCustomers.size.toString(), Color(0xFF4CAF50))
                }
            }
            
            // Buscador
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar clientes...") },
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
                        Text("Cargando clientes...")
                        Text(
                            text = "Cargando primera página (10 clientes)",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredCustomers) { customer ->
                        CustomerCard(
                            customer = customer,
                            onEdit = { onNavigateToEdit(customer) },
                            onDelete = {
                                customerToDelete = customer
                                showDeleteDialog = true
                            }
                        )
                    }
                    
                    // Mensaje si está vacío
                    if (filteredCustomers.isEmpty()) {
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
                                            "No hay clientes disponibles"
                                        } else {
                                            "No se encontraron clientes"
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
                                            onClick = { loadFirstPage() },
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
    if (showDeleteDialog && customerToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Cliente") },
            text = { Text("¿Estás seguro de eliminar a ${customerToDelete!!.custName} ${customerToDelete!!.custLastName}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        isLoading = true
                        apiService.deleteCustomer(customerToDelete!!.idCustomer, object : ApiService.ApiCallback<Boolean> {
                            override fun onSuccess(data: Boolean) {
                                scope.launch(Dispatchers.Main) {
                                    Toast.makeText(context, "✅ Cliente eliminado", Toast.LENGTH_SHORT).show()
                                    loadFirstPage()
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
fun CustomerCard(
    customer: Customer,
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
                        text = "${customer.custName} ${customer.custLastName}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF666666), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = customer.custPhone, fontSize = 14.sp, color = Color(0xFF666666))
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF666666), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = customer.custEmail, fontSize = 14.sp, color = Color(0xFF666666))
                    }
                    
                    if (!customer.custAddress.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF666666), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = customer.custAddress, fontSize = 14.sp, color = Color(0xFF666666))
                        }
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

@Composable
fun StatsItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 14.sp, color = Color(0xFF666666))
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
