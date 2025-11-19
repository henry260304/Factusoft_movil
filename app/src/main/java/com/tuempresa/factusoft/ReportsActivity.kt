package com.tuempresa.factusoft

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.FileProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import android.content.Context
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportsActivity : ComponentActivity() {
    
    private val saleDetailApiService = SaleDetailApiService()
    private val productReportApiService = ProductReportApiService()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                ReportsScreen(
                    saleDetailApiService = saleDetailApiService,
                    productReportApiService = productReportApiService,
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    saleDetailApiService: SaleDetailApiService,
    productReportApiService: ProductReportApiService,
    onBackPressed: () -> Unit
) {
    var showSaleDetailsReport by remember { mutableStateOf(false) }
    var showProductListReport by remember { mutableStateOf(false) }
    
    if (showSaleDetailsReport) {
        SaleDetailsReportScreen(
            apiService = saleDetailApiService,
            onBackPressed = { showSaleDetailsReport = false }
        )
    } else if (showProductListReport) {
        ProductListReportScreen(
            apiService = productReportApiService,
            onBackPressed = { showProductListReport = false }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Reportes") },
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Reportes Disponibles",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                item {
                    ReportCard(
                        icon = Icons.Default.Assessment,
                        title = "DETALLES DE VENTAS",
                        description = "Análisis detallado de ventas por período",
                        color = Color(0xFF4CAF50),
                        onClick = { showSaleDetailsReport = true }
                    )
                }
                
                item {
                    ReportCard(
                        icon = Icons.Default.Inventory,
                        title = "LISTA DE PRODUCTOS",
                        description = "Listado completo de todos los artículos en inventario",
                        color = Color(0xFF2196F3),
                        onClick = { showProductListReport = true }
                    )
                }
                
                item {
                    ReportCard(
                        icon = Icons.Default.ShoppingBag,
                        title = "Reporte de Compras",
                        description = "Historial y estadísticas de compras",
                        color = Color(0xFFFF9800)
                    )
                }
                
                item {
                    ReportCard(
                        icon = Icons.Default.People,
                        title = "Reporte de Clientes",
                        description = "Análisis de clientes y comportamiento de compra",
                        color = Color(0xFF9C27B0)
                    )
                }
                
                item {
                    ReportCard(
                        icon = Icons.Default.Business,
                        title = "Reporte de Proveedores",
                        description = "Evaluación de proveedores y pedidos",
                        color = Color(0xFF00BCD4)
                    )
                }
                
                item {
                    ReportCard(
                        icon = Icons.Default.TrendingUp,
                        title = "Reporte Financiero",
                        description = "Estado financiero y proyecciones",
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

@Composable
fun ReportCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    color: Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = color.copy(alpha = 0.1f)
                ),
                modifier = Modifier.size(60.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
            
            if (onClick != null) {
                IconButton(onClick = onClick) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Ver reporte",
                        tint = color
                    )
                }
            } else {
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Ver reporte",
                        tint = color
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleDetailsReportScreen(
    apiService: SaleDetailApiService,
    onBackPressed: () -> Unit
) {
    var saleDetail by remember { mutableStateOf<SaleDetail?>(null) }
    var saleIdInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showDetails by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Función para cargar detalles de venta por ID
    fun loadSaleDetail() {
        val saleId = saleIdInput.trim().toIntOrNull()
        if (saleId == null) {
            errorMessage = "Por favor ingresa un ID de venta válido"
            hasError = true
            Toast.makeText(context, "❌ ID de venta inválido", Toast.LENGTH_SHORT).show()
            return
        }
        
        hasError = false
        isLoading = true
        showDetails = false
        
        apiService.getSaleDetailById(saleId, object : SaleDetailApiService.ApiCallback<SaleDetail> {
            override fun onSuccess(data: SaleDetail) {
                scope.launch(Dispatchers.Main) {
                    saleDetail = data
                    isLoading = false
                    hasError = false
                    showDetails = true
                    Toast.makeText(context, "✅ Detalle de venta cargado", Toast.LENGTH_SHORT).show()
                }
            }
            
            override fun onError(error: String) {
                scope.launch(Dispatchers.Main) {
                    isLoading = false
                    hasError = true
                    errorMessage = error
                    showDetails = false
                    Toast.makeText(context, "❌ Error: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DETALLES DE VENTAS") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp),
                            color = Color.White
                        )
                    } else {
                        IconButton(onClick = { loadSaleDetail() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = Color.White)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Campo para ingresar ID de venta
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Consulta de Detalle de Venta",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    OutlinedTextField(
                        value = saleIdInput,
                        onValueChange = { saleIdInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("ID de Venta") },
                        placeholder = { Text("Ej: 1") },
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = "ID Venta")
                        },
                        trailingIcon = {
                            if (saleIdInput.isNotEmpty()) {
                                IconButton(onClick = { saleIdInput = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Limpiar")
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = { loadSaleDetail() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && saleIdInput.trim().isNotEmpty()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Consultando...")
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Consultar")
                        }
                    }
                }
            }
            
            // Contenido
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando detalles de ventas...")
                    }
                }
            } else if (hasError) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            Icons.Default.CloudOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFFF44336)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Error al conectar con el servidor",
                            color = Color(0xFFC62828),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            fontSize = 12.sp,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { loadSaleDetail() },
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
            } else if (showDetails && saleDetail != null) {
                // Mostrar detalles de la venta
                val detail = saleDetail!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Información general de la venta
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Información de la Venta",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF333333),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                
                                InfoRow("Factura ID:", detail.facturaId.toString())
                                InfoRow("Fecha:", formatDate(detail.fecha))
                                InfoRow("Cliente:", detail.cliente.firstOrNull()?.let { 
                                    "${it.nombre} ${it.apellido}" 
                                } ?: "-")
                                InfoRow("Vendedor:", detail.vendedor.firstOrNull()?.nombreCompleto ?: "-")
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                InfoRow("Total:", String.format("$%.2f", detail.total), Color(0xFF4CAF50), FontWeight.Bold)
                            }
                        }
                    }
                    
                    // Tabla de productos vendidos
                    item {
                        Text(
                            text = "Productos Vendidos",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    // Encabezado de la tabla de productos
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF2196F3))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Producto",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(2f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Cant.",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(0.8f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "P. Unitario",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Subtotal",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    // Lista de productos
                    items(detail.productosVendidos) { producto ->
                        ProductRow(producto)
                    }
                }
            } else {
                // Estado inicial - mostrar instrucciones
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF2196F3)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Consulta de Detalle de Venta",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ingresa el ID de la venta para ver los detalles",
                            fontSize = 14.sp,
                            color = Color(0xFF666666),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, valueColor: Color = Color(0xFF333333), valueWeight: FontWeight = FontWeight.Normal) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF666666),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = valueColor,
            fontWeight = valueWeight
        )
    }
}

@Composable
fun ProductRow(producto: ProductoVendido) {
    val subtotal = producto.cantidad * producto.precioUnitario
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, Color(0xFFE0E0E0))
            .padding(12.dp)
    ) {
        Text(
            text = producto.nombre,
            fontSize = 12.sp,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.Start,
            maxLines = 2
        )
        Text(
            text = producto.cantidad.toString(),
            fontSize = 12.sp,
            modifier = Modifier.weight(0.8f),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            text = String.format("$%.2f", producto.precioUnitario),
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            text = String.format("$%.2f", subtotal),
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontWeight = FontWeight.Medium
        )
    }
}

fun formatDate(dateString: String): String {
    return try {
        // Formato original: 2025-04-30T00:31:42.771000
        // Formato deseado: 30/04/2025 00:31
        val parts = dateString.split("T")
        if (parts.size == 2) {
            val datePart = parts[0].split("-") // [2025, 04, 30]
            val timePart = parts[1].split(":") // [00, 31, 42.771000]
            if (datePart.size == 3 && timePart.size >= 2) {
                "${datePart[2]}/${datePart[1]}/${datePart[0]} ${timePart[0]}:${timePart[1]}"
            } else {
                dateString
            }
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListReportScreen(
    apiService: ProductReportApiService,
    onBackPressed: () -> Unit
) {
    var products by remember { mutableStateOf<List<ProductReport>>(emptyList()) }
    var filteredProducts by remember { mutableStateOf<List<ProductReport>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Función para cargar productos
    fun loadProducts() {
        hasError = false
        isLoading = true
        
        apiService.getAllProducts(object : ProductReportApiService.ApiCallback<List<ProductReport>> {
            override fun onSuccess(data: List<ProductReport>) {
                scope.launch(Dispatchers.Main) {
                    products = data
                    filteredProducts = data
                    isLoading = false
                    hasError = false
                    Toast.makeText(context, "✅ ${data.size} productos cargados", Toast.LENGTH_SHORT).show()
                }
            }
            
            override fun onError(error: String) {
                scope.launch(Dispatchers.Main) {
                    isLoading = false
                    hasError = true
                    errorMessage = error
                    products = emptyList()
                    filteredProducts = emptyList()
                    Toast.makeText(context, "❌ Error: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
    
    // Efecto para cargar productos al iniciar
    LaunchedEffect(Unit) {
        loadProducts()
    }
    
    // Efecto para filtrar productos cuando cambia la búsqueda
    LaunchedEffect(searchQuery) {
        filteredProducts = if (searchQuery.isBlank()) {
            products
        } else {
            val query = searchQuery.lowercase()
            products.filter {
                it.nombreProducto.lowercase().contains(query) ||
                it.categoria.lowercase().contains(query) ||
                it.proveedor.lowercase().contains(query)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LISTA DE PRODUCTOS") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    if (!isLoading && products.isNotEmpty()) {
                        IconButton(onClick = {
                            val pdfFile = PdfGenerator.generateProductReportPdf(context, products)
                            if (pdfFile != null) {
                                sharePdfFile(context, pdfFile)
                            }
                        }) {
                            Icon(Icons.Default.Description, contentDescription = "Generar PDF", tint = Color.White)
                        }
                    }
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp),
                            color = Color.White
                        )
                    } else {
                        IconButton(onClick = { loadProducts() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = Color.White)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Barra de búsqueda
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Buscar producto, categoría o proveedor") },
                        placeholder = { Text("Ej: Bebidas, Snacks...") },
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
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                }
            }
            
            // Contador de productos
            if (!isLoading && !hasError) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Text(
                        text = "Total de productos: ${filteredProducts.size}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            
            // Contenido
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando productos...")
                    }
                }
            } else if (hasError) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            Icons.Default.CloudOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFFF44336)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Error al conectar con el servidor",
                            color = Color(0xFFC62828),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            fontSize = 12.sp,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { loadProducts() },
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
            } else if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF9E9E9E)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No se encontraron productos" else "No hay productos",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Intenta con otra búsqueda" else "No hay productos disponibles",
                            fontSize = 14.sp,
                            color = Color(0xFF666666),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Tabla de productos
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // Encabezado de la tabla
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF2196F3))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Producto",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(2f),
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = "Precio",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(0.8f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Stock",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(0.7f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Categoría",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1.2f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Proveedor",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1.8f),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                    
                    // Lista de productos
                    items(filteredProducts) { product ->
                        ProductReportRow(product)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductReportRow(product: ProductReport) {
    val stockColor = if (product.stock < 100) Color(0xFFF44336) else Color(0xFF4CAF50)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
        Text(
            text = product.nombreProducto,
            fontSize = 12.sp,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.Start,
            maxLines = 2,
            color = Color(0xFF333333)
        )
        Text(
            text = String.format("$%.2f", product.precioVenta),
            fontSize = 12.sp,
            modifier = Modifier.weight(0.8f),
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333)
        )
        Text(
            text = product.stock.toString(),
            fontSize = 12.sp,
            modifier = Modifier.weight(0.7f),
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontWeight = FontWeight.Medium,
            color = stockColor
        )
        Text(
            text = product.categoria,
            fontSize = 12.sp,
            modifier = Modifier.weight(1.2f),
            textAlign = TextAlign.Center,
            maxLines = 1,
            color = Color(0xFF666666)
        )
        Text(
            text = product.proveedor,
            fontSize = 11.sp,
            modifier = Modifier.weight(1.8f),
            textAlign = TextAlign.Start,
            maxLines = 2,
            color = Color(0xFF666666)
        )
        }
        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
    }
}

/**
 * Comparte un archivo PDF usando Intent
 */
fun sharePdfFile(context: Context, file: File) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Reporte de Productos")
            putExtra(Intent.EXTRA_TEXT, "Adjunto encontrarás el reporte de productos generado desde FactuSoft.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Compartir PDF"))
        Toast.makeText(context, "✅ PDF generado exitosamente", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "❌ Error al compartir PDF: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
