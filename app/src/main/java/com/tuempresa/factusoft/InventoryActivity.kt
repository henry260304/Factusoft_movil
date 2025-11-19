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

class InventoryActivity : ComponentActivity() {
    
    private val apiService = ProductApiService()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                InventoryScreen(
                    apiService = apiService,
                    onBackPressed = { finish() },
                    onNavigateToCreate = {
                        startActivity(Intent(this, ProductCreateActivity::class.java))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    apiService: ProductApiService,
    onBackPressed: () -> Unit,
    onNavigateToCreate: () -> Unit
) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var filteredProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
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
        
        apiService.getAllProducts(object : ProductApiService.ApiCallback<List<Product>> {
            override fun onSuccess(data: List<Product>) {
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
    
    // Cargar al iniciar
    LaunchedEffect(Unit) {
        loadProducts()
    }
    
    // Filtrar productos
    LaunchedEffect(searchQuery) {
        filteredProducts = if (searchQuery.isEmpty()) {
            products
        } else {
            products.filter { product ->
                product.nameProduct.contains(searchQuery, ignoreCase = true) ||
                (product.description?.contains(searchQuery, ignoreCase = true) == true) ||
                (product.category?.nameCategory?.contains(searchQuery, ignoreCase = true) == true) ||
                (product.supplier?.nameSupplier?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { loadProducts() }) {
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
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Producto", tint = Color.White)
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
                    StatsItem("Total", products.size.toString(), Color(0xFF2196F3))
                    StatsItem("Filtrados", filteredProducts.size.toString(), Color(0xFF4CAF50))
                }
            }
            
            // Buscador
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar productos...") },
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
                        Text("Cargando productos...")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredProducts) { product ->
                        ProductCard(product = product)
                    }
                    
                    // Mensaje si está vacío
                    if (filteredProducts.isEmpty()) {
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
                                        if (hasError) Icons.Default.CloudOff else Icons.Default.Inventory,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = if (hasError) Color(0xFFF44336) else Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = if (hasError) {
                                            "Error al conectar con el servidor"
                                        } else if (searchQuery.isEmpty()) {
                                            "No hay productos disponibles"
                                        } else {
                                            "No se encontraron productos"
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
                                            onClick = { loadProducts() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF2196F3)
                                            )
                                        ) {
                                            Icon(Icons.Default.Refresh, null)
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
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.nameProduct,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Categoría
                    product.category?.let {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE3F2FD)
                            )
                        ) {
                            Text(
                                text = it.nameCategory,
                                fontSize = 12.sp,
                                color = Color(0xFF1976D2),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Proveedor
                    product.supplier?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Business, null, tint = Color(0xFF666666), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = it.nameSupplier, fontSize = 12.sp, color = Color(0xFF666666))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Precios
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column {
                            Text("Costo", fontSize = 12.sp, color = Color.Gray)
                            Text("$${product.priceCost}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                        }
                        Column {
                            Text("Venta", fontSize = 12.sp, color = Color.Gray)
                            Text("$${product.priceSelling}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                        }
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    // Stock
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (product.currentStock <= product.minStock) {
                                Color(0xFFFFEBEE)
                            } else {
                                Color(0xFFE8F5E9)
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Stock", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = product.currentStock.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (product.currentStock <= product.minStock) {
                                    Color(0xFFF44336)
                                } else {
                                    Color(0xFF4CAF50)
                                }
                            )
                            Text("Min: ${product.minStock}", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
