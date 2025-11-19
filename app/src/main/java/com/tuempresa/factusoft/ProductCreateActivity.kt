package com.tuempresa.factusoft

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductCreateActivity : ComponentActivity() {
    
    private val apiService = ProductApiService()
    private val categoryApiService = CategoryApiService()
    private val supplierApiService = SupplierApiService()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                ProductCreateScreen(
                    apiService = apiService,
                    categoryApiService = categoryApiService,
                    supplierApiService = supplierApiService,
                    onBackPressed = { finish() },
                    onProductCreated = {
                        Toast.makeText(this, "✅ Producto creado exitosamente", Toast.LENGTH_LONG).show()
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCreateScreen(
    apiService: ProductApiService,
    categoryApiService: CategoryApiService,
    supplierApiService: SupplierApiService,
    onBackPressed: () -> Unit,
    onProductCreated: () -> Unit
) {
    var nameProduct by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceCost by remember { mutableStateOf("") }
    var priceSelling by remember { mutableStateOf("") }
    var currentStock by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("15") }
    
    // Estados para los selectores
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var suppliers by remember { mutableStateOf<List<Supplier>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedSupplier by remember { mutableStateOf<Supplier?>(null) }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedSupplier by remember { mutableStateOf(false) }
    
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingData by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Cargar categorías y proveedores al iniciar
    LaunchedEffect(Unit) {
        // Cargar categorías
        categoryApiService.getAllCategories(object : CategoryApiService.ApiCallback<List<Category>> {
            override fun onSuccess(data: List<Category>) {
                scope.launch(Dispatchers.Main) {
                    categories = data
                    isLoadingData = false
                }
            }
            
            override fun onError(error: String) {
                scope.launch(Dispatchers.Main) {
                    isLoadingData = false
                    Toast.makeText(context, "⚠️ No se pudieron cargar las categorías", Toast.LENGTH_SHORT).show()
                }
            }
        })
        
        // Cargar proveedores
        supplierApiService.getAllSuppliers(object : SupplierApiService.ApiCallback<List<Supplier>> {
            override fun onSuccess(data: List<Supplier>) {
                scope.launch(Dispatchers.Main) {
                    suppliers = data
                }
            }
            
            override fun onError(error: String) {
                scope.launch(Dispatchers.Main) {
                    Toast.makeText(context, "⚠️ No se pudieron cargar los proveedores", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Producto") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
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
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Mostrar error si hay
            if (errorMessage.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = "⚠️ $errorMessage",
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            if (isLoadingData) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Cargando categorías y proveedores...")
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Nombre del producto
                        OutlinedTextField(
                            value = nameProduct,
                            onValueChange = { nameProduct = it },
                            label = { Text("Nombre del Producto *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Inventory, null) }
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Descripción
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descripción (opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5,
                            leadingIcon = { Icon(Icons.Default.Description, null) }
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Selector de Categoría (OBLIGATORIO)
                        ExposedDropdownMenuBox(
                            expanded = expandedCategory,
                            onExpandedChange = { expandedCategory = !expandedCategory }
                        ) {
                            OutlinedTextField(
                                value = selectedCategory?.nameCategory ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Categoría *") },
                                placeholder = { Text("Selecciona una categoría") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                                },
                                leadingIcon = { Icon(Icons.Default.Category, null) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                isError = selectedCategory == null && errorMessage.contains("categoría")
                            )
                            
                            ExposedDropdownMenu(
                                expanded = expandedCategory,
                                onDismissRequest = { expandedCategory = false }
                            ) {
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category.nameCategory) },
                                        onClick = {
                                            selectedCategory = category
                                            expandedCategory = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Selector de Proveedor (OBLIGATORIO)
                        ExposedDropdownMenuBox(
                            expanded = expandedSupplier,
                            onExpandedChange = { expandedSupplier = !expandedSupplier }
                        ) {
                            OutlinedTextField(
                                value = selectedSupplier?.nameSupplier ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Proveedor *") },
                                placeholder = { Text("Selecciona un proveedor") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSupplier)
                                },
                                leadingIcon = { Icon(Icons.Default.Business, null) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                isError = selectedSupplier == null && errorMessage.contains("proveedor")
                            )
                            
                            ExposedDropdownMenu(
                                expanded = expandedSupplier,
                                onDismissRequest = { expandedSupplier = false }
                            ) {
                                suppliers.forEach { supplier ->
                                    DropdownMenuItem(
                                        text = { Text(supplier.nameSupplier) },
                                        onClick = {
                                            selectedSupplier = supplier
                                            expandedSupplier = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Precio de costo
                        OutlinedTextField(
                            value = priceCost,
                            onValueChange = { priceCost = it },
                            label = { Text("Precio de Costo *") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            prefix = { Text("$") },
                            leadingIcon = { Icon(Icons.Default.AttachMoney, null) }
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Precio de venta
                        OutlinedTextField(
                            value = priceSelling,
                            onValueChange = { priceSelling = it },
                            label = { Text("Precio de Venta *") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            prefix = { Text("$") },
                            leadingIcon = { Icon(Icons.Default.Sell, null) }
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Stock actual
                            OutlinedTextField(
                                value = currentStock,
                                onValueChange = { currentStock = it },
                                label = { Text("Stock Actual *") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Warehouse, null) }
                            )
                            
                            // Stock mínimo
                            OutlinedTextField(
                                value = minStock,
                                onValueChange = { minStock = it },
                                label = { Text("Stock Mínimo *") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Warning, null) }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Botones
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onBackPressed,
                                modifier = Modifier.weight(1f),
                                enabled = !isLoading
                            ) {
                                Icon(Icons.Default.Close, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cancelar")
                            }
                            
                            Button(
                                onClick = {
                                    // Capturar valores locales para validación
                                    val category = selectedCategory
                                    val supplier = selectedSupplier
                                    
                                    // Validación
                                    if (nameProduct.isBlank()) {
                                        errorMessage = "El nombre es obligatorio"
                                        return@Button
                                    }
                                    
                                    if (category == null) {
                                        errorMessage = "Debes seleccionar una categoría"
                                        return@Button
                                    }
                                    
                                    if (supplier == null) {
                                        errorMessage = "Debes seleccionar un proveedor"
                                        return@Button
                                    }
                                    
                                    if (priceCost.isBlank() || priceCost.toDoubleOrNull() == null) {
                                        errorMessage = "El precio de costo debe ser un número válido"
                                        return@Button
                                    }
                                    
                                    if (priceSelling.isBlank() || priceSelling.toDoubleOrNull() == null) {
                                        errorMessage = "El precio de venta debe ser un número válido"
                                        return@Button
                                    }
                                    
                                    if (currentStock.isBlank() || currentStock.toIntOrNull() == null) {
                                        errorMessage = "El stock actual debe ser un número válido"
                                        return@Button
                                    }
                                    
                                    if (minStock.isBlank() || minStock.toIntOrNull() == null) {
                                        errorMessage = "El stock mínimo debe ser un número válido"
                                        return@Button
                                    }
                                    
                                    errorMessage = ""
                                    isLoading = true
                                    
                                    val newProduct = NewProduct(
                                        nameProduct = nameProduct.trim(),
                                        description = description.takeIf { it.isNotBlank() },
                                        priceCost = priceCost,
                                        priceSelling = priceSelling,
                                        currentStock = currentStock.toInt(),
                                        minStock = minStock.toInt(),
                                        categoryId = category.idCategory,
                                        supplierId = supplier.idSupplier
                                    )
                                    
                                    Log.d("ProductCreate", "Datos a enviar:")
                                    Log.d("ProductCreate", "  Nombre: ${newProduct.nameProduct}")
                                    Log.d("ProductCreate", "  Descripción: ${newProduct.description}")
                                    Log.d("ProductCreate", "  Precio costo: ${newProduct.priceCost}")
                                    Log.d("ProductCreate", "  Precio venta: ${newProduct.priceSelling}")
                                    Log.d("ProductCreate", "  Stock actual: ${newProduct.currentStock}")
                                    Log.d("ProductCreate", "  Stock mínimo: ${newProduct.minStock}")
                                    Log.d("ProductCreate", "  Categoría ID: ${newProduct.categoryId}")
                                    Log.d("ProductCreate", "  Proveedor ID: ${newProduct.supplierId}")
                                    
                                    apiService.createProduct(newProduct, object : ProductApiService.ApiCallback<Product> {
                                        override fun onSuccess(data: Product) {
                                            scope.launch(Dispatchers.Main) {
                                                isLoading = false
                                                onProductCreated()
                                            }
                                        }
                                        
                                        override fun onError(error: String) {
                                            scope.launch(Dispatchers.Main) {
                                                isLoading = false
                                                errorMessage = "Error al guardar:\n$error"
                                                Toast.makeText(context, "❌ Error al guardar producto", Toast.LENGTH_LONG).show()
                                                
                                                // Log adicional para debug
                                                Log.e("ProductCreate", "Error completo: $error")
                                            }
                                        }
                                    })
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !isLoading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50)
                                )
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Default.Save, null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Guardar")
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Información de ayuda
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ℹ️ Información:", color = Color(0xFF1976D2))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Los campos marcados con * son obligatorios", color = Color(0xFF666666))
                    Text("• Categoría y proveedor son OBLIGATORIOS", color = Color(0xFFE65100))
                    Text("• Usa punto (.) para decimales, no coma", color = Color(0xFF666666))
                    if (categories.isNotEmpty()) {
                        Text("✅ ${categories.size} categorías disponibles", color = Color(0xFF4CAF50))
                    }
                    if (suppliers.isNotEmpty()) {
                        Text("✅ ${suppliers.size} proveedores disponibles", color = Color(0xFF4CAF50))
                    }
                }
            }
        }
    }
}
