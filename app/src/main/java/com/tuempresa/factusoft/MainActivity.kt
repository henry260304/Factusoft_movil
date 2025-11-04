package com.tuempresa.factusoft

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Verificar si el usuario está logueado
        val sharedPreferences = getSharedPreferences("FactusoftLogin", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        
        if (!isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        setContent {
            MaterialTheme {
                MainApp(
                    onLogout = {
                        val editor = sharedPreferences.edit()
                        editor.clear()
                        editor.apply()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    },
                    username = sharedPreferences.getString("username", "Usuario") ?: "Usuario",
                    loginTime = sharedPreferences.getLong("loginTime", 0),
                    onNavigateToCustomers = {
                        startActivity(Intent(this, CustomersActivity::class.java))
                    },
                    onNavigateToSuppliers = {
                        startActivity(Intent(this, SuppliersActivity::class.java))
                    },
                    onNavigateToInventory = {
                        startActivity(Intent(this, InventoryActivity::class.java))
                    },
                    onNavigateToSales = {
                        startActivity(Intent(this, SalesActivity::class.java))
                    },
                    onNavigateToPurchases = {
                        startActivity(Intent(this, PurchasesActivity::class.java))
                    },
                    onNavigateToReports = {
                        startActivity(Intent(this, ReportsActivity::class.java))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    onLogout: () -> Unit,
    username: String,
    loginTime: Long,
    onNavigateToCustomers: () -> Unit,
    onNavigateToSuppliers: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToSales: () -> Unit,
    onNavigateToPurchases: () -> Unit,
    onNavigateToReports: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                DrawerContent(
                    username = username,
                    onItemClick = { destination ->
                        scope.launch {
                            drawerState.close()
                            when (destination) {
                                "home" -> { /* Ya estamos en home */ }
                                "inventory" -> onNavigateToInventory()
                                "sales" -> onNavigateToSales()
                                "purchases" -> onNavigateToPurchases()
                                "customers" -> onNavigateToCustomers()
                                "suppliers" -> onNavigateToSuppliers()
                                "reports" -> onNavigateToReports()
                                "settings" -> Toast.makeText(context, "Configuración próximamente", Toast.LENGTH_SHORT).show()
                                "logout" -> onLogout()
                            }
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("FactuSoft") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val loginDate = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                                .format(java.util.Date(loginTime))
                            Toast.makeText(context, "Usuario: $username\nÚltimo acceso: $loginDate", Toast.LENGTH_LONG).show()
                        }) {
                            Icon(Icons.Default.Info, contentDescription = "Info")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            DashboardScreen(modifier = Modifier.padding(paddingValues))
        }
    }
}

@Composable
fun DrawerContent(
    username: String,
    onItemClick: (String) -> Unit
) {
    Column {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2196F3),
                            Color(0xFF1976D2)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_factusoft),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(56.dp)
                        .padding(end = 16.dp)
                )
                Column {
                    Text(
                        text = "FactuSoft",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Sistema de Gestión Empresarial",
                        fontSize = 13.sp,
                        color = Color(0xFFE0E0E0)
                    )
                }
            }
        }
        
        Divider(thickness = 2.dp, color = Color(0xFFBDBDBD), modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
        
        // Menú items
        DrawerMenuItem(icon = Icons.Default.Home, title = "Inicio", onClick = { onItemClick("home") })
        DrawerMenuItem(icon = Icons.Default.Inventory, title = "Inventario", onClick = { onItemClick("inventory") })
        DrawerMenuItem(icon = Icons.Default.ShoppingCart, title = "Ventas", onClick = { onItemClick("sales") })
        DrawerMenuItem(icon = Icons.Default.ShoppingBag, title = "Compras", onClick = { onItemClick("purchases") })
        DrawerMenuItem(icon = Icons.Default.People, title = "Clientes", onClick = { onItemClick("customers") })
        DrawerMenuItem(icon = Icons.Default.Business, title = "Proveedores", onClick = { onItemClick("suppliers") })
        DrawerMenuItem(icon = Icons.Default.Assessment, title = "Reportes", onClick = { onItemClick("reports") })
        
        Spacer(modifier = Modifier.weight(1f))
        
        Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        DrawerMenuItem(icon = Icons.Default.Settings, title = "Configuración", onClick = { onItemClick("settings") })
        DrawerMenuItem(icon = Icons.Default.Logout, title = "Cerrar Sesión", onClick = { onItemClick("logout") })
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF666666)
        )
        Spacer(modifier = Modifier.width(32.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color(0xFF333333)
        )
    }
}

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Resumen del Día",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Cards de resumen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardCard(
                title = "Ventas Hoy",
                value = "$2,450.00",
                valueColor = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
            DashboardCard(
                title = "Productos",
                value = "156",
                valueColor = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )
        }
        
        // Productos con Stock Bajo
        SectionTitle("Productos con Stock Bajo")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                LowStockItem("Mouse Inalámbrico", minStock = 10, currentStock = 3)
                LowStockItem("Silla de Oficina", minStock = 3, currentStock = 0, isOutOfStock = true)
                LowStockItem("Teclado Mecánico", minStock = 5, currentStock = 2, isLast = true)
            }
        }
        
        // Actividad Reciente
        SectionTitle("Actividad Reciente")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ActivityItem("Venta #1 - Cliente: Juan Pérez", "10:30 AM", "$850", Color(0xFF4CAF50))
                ActivityItem("Compra #1 - Proveedor: TechMax", "09:15 AM", "$1200", Color(0xFFFF9800))
                ActivityItem("Nuevo cliente: María García", "08:45 AM", null, null)
                ActivityItem("Venta #2 - Cliente: Carlos López", "08:20 AM", "$120", Color(0xFF4CAF50), isLast = true)
            }
        }
        
        // Compras Pendientes
        SectionTitle("Compras Pendientes")
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
                Text(
                    text = "Tienes compras pendientes por procesar",
                    fontSize = 16.sp,
                    color = Color(0xFF333333),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "12",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF333333),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun DashboardCard(title: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

@Composable
fun LowStockItem(name: String, minStock: Int, currentStock: Int, isOutOfStock: Boolean = false, isLast: Boolean = false) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isLast) 0.dp else 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                fontSize = 16.sp,
                color = Color(0xFF333333),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Mínimo: $minStock",
                fontSize = 12.sp,
                color = Color(0xFF666666),
                modifier = Modifier.padding(end = 8.dp)
            )
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isOutOfStock) Color(0xFFF44336) else Color(0xFF666666)
                )
            ) {
                Text(
                    text = "Stock: $currentStock",
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ActivityItem(description: String, time: String, amount: String?, amountColor: Color?, isLast: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = description,
            fontSize = 14.sp,
            color = Color(0xFF333333),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = time,
            fontSize = 12.sp,
            color = Color(0xFF666666),
            modifier = Modifier.padding(end = if (amount != null) 8.dp else 0.dp)
        )
        if (amount != null && amountColor != null) {
            Text(
                text = amount,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardScreenPreview() {
    MaterialTheme {
        DashboardScreen()
    }
}