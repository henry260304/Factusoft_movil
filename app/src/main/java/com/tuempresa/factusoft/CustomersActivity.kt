package com.tuempresa.factusoft

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

class CustomersActivity : ComponentActivity() {
    
    private val apiService = ApiService()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                CustomersScreen(
                    onBackPressed = { finish() },
                    apiService = apiService,
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
    
    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    onBackPressed: () -> Unit,
    apiService: ApiService,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Customer) -> Unit
) {
    var customers by remember { mutableStateOf<List<Customer>>(emptyList()) }
    var filteredCustomers by remember { mutableStateOf<List<Customer>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Cargar clientes al iniciar
    LaunchedEffect(Unit) {
        isLoading = true
        withContext(Dispatchers.IO) {
            apiService.getAllCustomers(object : ApiService.ApiCallback<List<Customer>> {
                override fun onSuccess(data: List<Customer>) {
                    customers = data
                    filteredCustomers = data
                    isLoading = false
                    Toast.makeText(context, "✅ ${data.size} clientes cargados", Toast.LENGTH_SHORT).show()
                }
                
                override fun onError(error: String) {
                    isLoading = false
                    if (error.contains("403")) {
                        Toast.makeText(context, "⚠️ Servidor detenido. Cargando datos de prueba...", Toast.LENGTH_LONG).show()
                        loadTestData { testCustomers ->
                            customers = testCustomers
                            filteredCustomers = testCustomers
                        }
                    } else {
                        Toast.makeText(context, "❌ Error: $error\nCargando datos de prueba...", Toast.LENGTH_LONG).show()
                        loadTestData { testCustomers ->
                            customers = testCustomers
                            filteredCustomers = testCustomers
                        }
                    }
                }
            })
        }
    }
    
    // Filtrar clientes cuando cambia la búsqueda
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
                    StatsItem("Total", customers.size.toString(), Color(0xFF2196F3))
                    StatsItem("Activos", customers.size.toString(), Color(0xFF4CAF50))
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
            
            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Lista de clientes
                LazyColumn(
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
                }
            }
        }
    }
    
    // Diálogo de confirmación de eliminación
    if (showDeleteDialog && customerToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Cliente") },
            text = { Text("¿Estás seguro de que quieres eliminar a ${customerToDelete!!.custName} ${customerToDelete!!.custLastName}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            withContext(Dispatchers.IO) {
                                apiService.deleteCustomer(customerToDelete!!.idCustomer, object : ApiService.ApiCallback<Boolean> {
                                    override fun onSuccess(data: Boolean) {
                                        Toast.makeText(context, "✅ Cliente eliminado exitosamente", Toast.LENGTH_SHORT).show()
                                        // Recargar lista
                                        apiService.getAllCustomers(object : ApiService.ApiCallback<List<Customer>> {
                                            override fun onSuccess(data: List<Customer>) {
                                                customers = data
                                                filteredCustomers = data
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
fun CustomerCard(
    customer: Customer,
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
                        text = "${customer.custName} ${customer.custLastName}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = "Teléfono",
                            tint = Color(0xFF666666),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = customer.custPhone,
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
                            text = customer.custEmail,
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                    }
                    
                    if (!customer.custAddress.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Dirección",
                                tint = Color(0xFF666666),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = customer.custAddress,
                                fontSize = 14.sp,
                                color = Color(0xFF666666)
                            )
                        }
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

@Composable
fun StatsItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF666666)
        )
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

private fun loadTestData(onLoaded: (List<Customer>) -> Unit) {
    val testCustomers = listOf(
        Customer(1, "Juan", "Pérez", "+52 123 456 7890", "juan.perez@email.com", "Av. Principal 123, CDMX"),
        Customer(2, "María", "García", "+52 123 456 7891", "maria.garcia@email.com", "Calle Reforma 456, Guadalajara"),
        Customer(3, "Carlos", "López", "+52 123 456 7892", "carlos.lopez@email.com", "Blvd. Insurgentes 789, Monterrey"),
        Customer(4, "Ana", "Martínez", "+52 123 456 7893", "ana.martinez@email.com", "Av. Juárez 321, Puebla"),
        Customer(5, "Luis", "Rodríguez", "+52 123 456 7894", "luis.rodriguez@email.com", "Calle Hidalgo 654, Querétaro"),
        Customer(6, "Carmen", "Silva", "+52 123 456 7895", "carmen.silva@email.com", "Av. Revolución 987, Tijuana"),
        Customer(7, "Roberto", "Torres", "+52 123 456 7896", "roberto.torres@email.com", "Calle Morelos 147, Mérida"),
        Customer(8, "Elena", "Vargas", "+52 123 456 7897", "elena.vargas@email.com", "Blvd. Universidad 258, León"),
        Customer(9, "Diego", "Morales", "+52 123 456 7898", "diego.morales@email.com", "Av. Constitución 369, Toluca"),
        Customer(10, "Patricia", "Ruiz", "+52 123 456 7899", "patricia.ruiz@email.com", "Calle Independencia 741, Cancún"),
        Customer(11, "Fernando", "Castro", "+52 123 456 7800", "fernando.castro@email.com", "Av. Chapultepec 852, CDMX"),
        Customer(12, "Sofia", "Ramírez", "+52 123 456 7801", "sofia.ramirez@email.com", "Calle Zaragoza 963, Guadalajara"),
        Customer(13, "Miguel", "Flores", "+52 123 456 7802", "miguel.flores@email.com", "Blvd. Díaz Ordaz 159, Monterrey"),
        Customer(14, "Laura", "Jiménez", "+52 123 456 7803", "laura.jimenez@email.com", "Av. Madero 357, Puebla"),
        Customer(15, "Jorge", "Hernández", "+52 123 456 7804", "jorge.hernandez@email.com", "Calle Guerrero 486, Querétaro")
    )
    onLoaded(testCustomers)
}
