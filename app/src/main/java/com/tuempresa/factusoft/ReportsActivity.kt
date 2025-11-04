package com.tuempresa.factusoft

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

class ReportsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                ReportsScreen(onBackPressed = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(onBackPressed: () -> Unit) {
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
                    title = "Reporte de Ventas",
                    description = "Análisis detallado de ventas por período",
                    color = Color(0xFF4CAF50)
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
                    icon = Icons.Default.Inventory,
                    title = "Reporte de Inventario",
                    description = "Estado actual del inventario y movimientos",
                    color = Color(0xFF2196F3)
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

@Composable
fun ReportCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
            
            IconButton(onClick = { /* Ver reporte */ }) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Ver reporte",
                    tint = color
                )
            }
        }
    }
}
