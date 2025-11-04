# 📱 Guía Visual: Ver Preview y Ejecutar en Emulador

## ✅ CAMBIO APLICADO

Acabo de actualizar `LoginActivity.kt` para que use la vista separada `LoginScreen.kt`.

Ahora tienes:
- 📄 `ui/screens/LoginScreen.kt` → VISTA (ver preview aquí)
- 📄 `LoginActivity.kt` → LÓGICA (simplificado a 60 líneas)

---

## 🎨 PARTE 1: Ver Preview (Sin Emulador)

### Paso 1: Abre el Archivo Correcto
Ya tienes abierto: ✅ `ui/screens/LoginScreen.kt`

### Paso 2: Encuentra los Botones de Vista

**En la parte SUPERIOR DERECHA del editor**, busca estos botones:

```
┌──────────────────────────────────────────────────┐
│ LoginScreen.kt          [Code] [Split] [Design] │ ← AQUÍ
└──────────────────────────────────────────────────┘
```

### Paso 3: Haz Clic en "Split"

Esto dividirá la pantalla:
- **Izquierda**: Código
- **Derecha**: Preview visual

### Paso 4: Espera a que Compile

Verás un mensaje en el panel derecho:
```
🔄 Building Previews...
```

Espera 10-30 segundos (primera vez puede tardar más)

### Paso 5: ¡Listo! 🎉

Deberías ver la pantalla de login completa con:
- Logo de FactuSoft
- Campos de usuario y contraseña
- Botón "INGRESAR"
- Credenciales por defecto

### 🔧 Si NO ves los Botones:

**Opción A:**
1. Menú: `View → Appearance → Toolbar` (activar)

**Opción B:**
1. Menú: `View → Tool Windows → Preview`
2. O presiona: `Alt + 8` (Windows/Linux)

**Opción C:**
1. Haz clic derecho en el código
2. Selecciona: `Show Preview`

---

## 🚀 PARTE 2: Ejecutar en Emulador

### Antes de Ejecutar:

#### 1. Sincroniza Gradle
En Android Studio:
```
File → Sync Project with Gradle Files
```
Espera a que termine (barra de progreso abajo)

#### 2. Limpia el Proyecto
```
Build → Clean Project
```
Espera unos segundos

#### 3. Reconstruye
```
Build → Rebuild Project
```
Espera 1-2 minutos (es normal)

### Ejecutar:

#### Paso 1: Asegúrate de que el Emulador Esté Corriendo

En la barra superior de Android Studio:
```
┌────────────────────────────────────┐
│ [▶️] [🔧] [Pixel 8 API 35 ▼]      │
│   ↑         ↑                      │
│  Run     Config  Emulador          │
└────────────────────────────────────┘
```

Si el emulador NO está corriendo:
1. Haz clic en el dropdown del emulador
2. Selecciona tu dispositivo
3. Espera a que arranque

#### Paso 2: Haz Clic en Run (▶️)

Verás mensajes en la parte inferior:
```
Building...
Installing APK...
Launching activity...
```

#### Paso 3: Espera la Instalación

Primera vez puede tardar 2-3 minutos. Verás:
```
BUILD SUCCESSFUL in 2m 15s
```

### ✅ ¿Qué Deberías Ver en el Emulador?

1. La app se abre automáticamente
2. Ves la pantalla de LOGIN
3. Puedes escribir en los campos
4. Puedes hacer login con:
   - Usuario: `admin`
   - Contraseña: `123456`

---

## ⚠️ Problemas Comunes y Soluciones

### Problema 1: "Cannot resolve symbol 'R'"

**Solución:**
```
1. Build → Clean Project
2. Build → Rebuild Project
3. File → Sync Project with Gradle Files
```

### Problema 2: La App se Cierra Inmediatamente

**Solución:**
1. Abre Logcat (parte inferior de Android Studio)
2. Filtra por: `com.tuempresa.factusoft`
3. Busca líneas en ROJO
4. Copia el error y me lo pasas

### Problema 3: No Veo el Preview

**Solución A:**
```kotlin
// Verifica que al final de LoginScreen.kt tengas:
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
```

**Solución B:**
1. Invalida la caché: `File → Invalidate Caches → Invalidate and Restart`

### Problema 4: "Building Previews" Tarda Mucho

Es normal la primera vez. Si tarda más de 3 minutos:
1. Cancela (botón X en el panel de preview)
2. Ve a: `File → Settings → Build → Compiler`
3. Aumenta el heap size a 2048 MB
4. Reinicia Android Studio

---

## 📊 Checklist Antes de Ejecutar

Marca cada paso:

- [ ] ✅ Gradle sincronizado
- [ ] ✅ Proyecto limpio (Clean Project)
- [ ] ✅ Proyecto reconstruido (Rebuild Project)
- [ ] ✅ Emulador corriendo
- [ ] ✅ Sin errores en el código (líneas rojas)
- [ ] ✅ LoginScreen.kt y LoginActivity.kt guardados

Si TODOS están marcados, haz clic en Run (▶️)

---

## 🎯 Comandos Alternativos (Terminal)

Si prefieres la terminal:

### Ver si hay errores:
```bash
gradlew.bat build --stacktrace
```

### Instalar en el emulador:
```bash
gradlew.bat installDebug
```

### Ver logs en tiempo real:
```bash
gradlew.bat installDebug && adb logcat | findstr "Factusoft"
```

---

## 📸 Ubicación Visual de Botones

```
┌─────────────────────────────────────────────────────────┐
│ File Edit View Navigate Code Analyze Refactor Build... │ ← Menú Principal
├─────────────────────────────────────────────────────────┤
│                                                         │
│ [▶️ Run] [🔧 Debug] [⏹️ Stop] [Pixel 8 API 35 ▼]       │ ← Barra de Herramientas
│                                                         │
├─────────────────────────────────────────────────────────┤
│                                                         │
│ LoginScreen.kt          [Code] [Split] [Design]        │ ← Tabs del Editor
│                                                         │
│ ┌────────────────┬──────────────────────────────────┐  │
│ │                │                                  │  │
│ │  TU CÓDIGO     │    PREVIEW AQUÍ                  │  │ ← Split View
│ │                │    (cuando clickeas Split)       │  │
│ │                │                                  │  │
│ └────────────────┴──────────────────────────────────┘  │
│                                                         │
├─────────────────────────────────────────────────────────┤
│ [Build] [Run] [TODO] [Logcat] [Terminal]               │ ← Panel Inferior
└─────────────────────────────────────────────────────────┘
```

---

## ✅ Próximos Pasos

Una vez que veas el preview y ejecutes en el emulador:

1. **Prueba el Login**:
   - Usuario: `admin`
   - Contraseña: `123456`

2. **Deberías ver**:
   - Dashboard con menú hamburguesa
   - Cards con estadísticas
   - Navegación funcionando

3. **Si funciona**:
   - ¡Felicidades! La migración a Compose está completa
   - Todas las pantallas funcionan igual

---

## 🆘 Si Sigue Sin Funcionar

Necesito que me digas:

1. ¿Ves los botones [Code] [Split] [Design]? ✅ Sí / ❌ No
2. ¿El preview se carga o da error? 
3. ¿Qué mensaje ves cuando das Run?
4. ¿La app se instala pero se cierra?
5. ¿Hay errores en Logcat? (copia las líneas rojas)

Con esa información te ayudo inmediatamente. 🚀

---

## 📝 Resumen Rápido

```
VER PREVIEW (sin emulador):
1. Abre: ui/screens/LoginScreen.kt
2. Click: [Split] (arriba derecha)
3. Espera: Building Previews...
4. ¡Listo! 🎉

EJECUTAR EN EMULADOR:
1. File → Sync Project with Gradle Files
2. Build → Clean Project
3. Build → Rebuild Project
4. Asegura que el emulador esté corriendo
5. Click: [▶️ Run]
6. Espera instalación (2-3 min primera vez)
7. ¡Listo! 🎉
```

¿En qué paso te atoras? 🤔

