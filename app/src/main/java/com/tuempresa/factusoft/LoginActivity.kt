package com.tuempresa.factusoft

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.tuempresa.factusoft.ui.screens.LoginScreen

/**
 * Activity de Login - Ahora SOLO contiene lógica
 * La vista está en: ui/screens/LoginScreen.kt
 */
class LoginActivity : ComponentActivity() {
    
    // Credenciales por defecto
    private val defaultUsername = "admin"
    private val defaultPassword = "123456"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                // ← Importa la vista desde ui/screens/LoginScreen.kt
                LoginScreen(
                    onLoginSuccess = { username ->
                        saveLoginState(username)
                        navigateToMain()
                    },
                    onShowError = { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    },
                    authenticateUser = { username, password ->
                        username == defaultUsername && password == defaultPassword
                    }
                )
            }
        }
    }
    
    private fun saveLoginState(username: String) {
        val sharedPreferences = getSharedPreferences("FactusoftLogin", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("username", username)
        editor.putLong("loginTime", System.currentTimeMillis())
        editor.apply()
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
