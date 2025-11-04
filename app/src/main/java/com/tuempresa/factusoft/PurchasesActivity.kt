package com.tuempresa.factusoft

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PurchasesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                PurchasesScreen(
                    onBackPressed = { finish() },
                    onNavigateToCreate = {
            startActivity(Intent(this, PurchaseCreateActivity::class.java))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchasesScreen(
    onBackPressed: () -> Unit,
    onNavigateToCreate: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compras") },
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
                containerColor = Color(0xFFFF9800)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Compra", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Estadísticas de compras
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Pendientes",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                        Text(
                            text = "12",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9800)
                        )
                    }
                    Divider(
                        modifier = Modifier
                            .height(50.dp)
                            .width(1.dp),
                        color = Color(0xFFE0E0E0)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Este Mes",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                        Text(
                            text = "$67,890",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )
                    }
                }
            }
            
            // Buscador
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar compras...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                singleLine = true
            )
            
            // Lista de compras
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(5) { index ->
                    PurchaseItemCard(
                        purchaseNumber = "#${2000 + index}",
                        supplierName = "Proveedor ${index + 1}",
                        amount = "$${(Math.random() * 2000 + 500).toInt()}",
                        date = "Hoy 09:${15 + index * 10} AM",
                        status = if (index % 2 == 0) "Completada" else "Pendiente"
                    )
                }
            }
        }
    }
}

@Composable
fun PurchaseItemCard(
    purchaseNumber: String,
    supplierName: String,
    amount: String,
    date: String,
    status: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = purchaseNumber,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Text(
                    text = supplierName,
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                Text(
                    text = date,
                    fontSize = 12.sp,
                    color = Color(0xFF999999)
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (status == "Completada") Color(0xFF4CAF50) else Color(0xFFFF9800)
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = status,
                        fontSize = 10.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = amount,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800)
            )
        }
    }
}
