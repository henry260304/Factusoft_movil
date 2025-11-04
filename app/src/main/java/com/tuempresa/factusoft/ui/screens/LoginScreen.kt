package com.tuempresa.factusoft.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuempresa.factusoft.R

/**
 * Pantalla de Login
 * Separada en su propio archivo para mejor organización
 */
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onShowError: (String) -> Unit,
    authenticateUser: (String, String) -> Boolean
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_factusoft),
                contentDescription = "Logo Factusoft",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )
            
            // Título
            Text(
                text = "Factusoft",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2c3e50)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtítulo
            Text(
                text = "Sistema de Facturación e Inventario",
                fontSize = 16.sp,
                color = Color(0xFF7f8c8d)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Card con formulario
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    // Campo de usuario
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            usernameError = null
                        },
                        label = { Text("Usuario") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "Usuario")
                        },
                        isError = usernameError != null,
                        supportingText = {
                            usernameError?.let { Text(it) }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Campo de contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Contraseña")
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = passwordError != null,
                        supportingText = {
                            passwordError?.let { Text(it) }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Botón de login
                    Button(
                        onClick = {
                            // Validación
                            var isValid = true
                            
                            if (username.isEmpty()) {
                                usernameError = "El usuario es requerido"
                                isValid = false
                            }
                            
                            if (password.isEmpty()) {
                                passwordError = "La contraseña es requerida"
                                isValid = false
                            } else if (password.length < 6) {
                                passwordError = "La contraseña debe tener al menos 6 caracteres"
                                isValid = false
                            }
                            
                            if (isValid) {
                                if (authenticateUser(username, password)) {
                                    onLoginSuccess(username)
                                } else {
                                    onShowError("Usuario o contraseña incorrectos")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3498db)
                        )
                    ) {
                        Text(
                            text = "INGRESAR",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Información de credenciales
            Text(
                text = "Credenciales por defecto:",
                fontSize = 14.sp,
                color = Color(0xFF95a5a6),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFecf0f1)
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Usuario: admin\nContraseña: 123456",
                    fontSize = 12.sp,
                    color = Color(0xFF95a5a6),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(
            onLoginSuccess = { },
            onShowError = { },
            authenticateUser = { _, _ -> true }
        )
    }
}

