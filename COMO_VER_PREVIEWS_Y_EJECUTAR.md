# 📱 Cómo Ver Previews y Ejecutar la App

## 1️⃣ Ver Previews en Android Studio (Sin Emulador)

### ✅ Previews Ya Agregados:
He agregado funciones `@Preview` a las pantallas principales:
- ✅ `LoginActivity.kt` → `LoginScreenPreview()`
- ✅ `MainActivity.kt` → `DashboardScreenPreview()`

### 📋 Cómo Ver los Previews:

1. **Abre cualquier archivo con @Preview** (por ejemplo, `LoginActivity.kt`)

2. **Busca el ícono "Split" o "Design"** en la esquina superior derecha del editor:
   ```
   [Code] [Split] [Design]
   ```

3. **Haz clic en "Split"** para ver código y preview juntos, o **"Design"** para ver solo el preview

4. **Espera a que se construya** (Build) - verás un mensaje "Building Previews..."

5. **¡Listo!** Verás la pantalla renderizada sin necesidad del emulador

### 🎨 Shortcuts:
- **Windows/Linux**: `Alt + 8` para abrir/cerrar el panel de preview
- **Mac**: `Cmd + 8`

---

## 2️⃣ Solucionar Problema del Emulador

Si la app no aparece en el emulador, aquí está la solución:

### 🔍 Diagnóstico:

**Paso 1: Verifica que el proyecto compile**
```bash
# En la terminal de Android Studio:
./gradlew clean build
```

Si hay errores, los verá aquí.

### ⚠️ Errores Comunes y Soluciones:

#### Error 1: "No se encuentra R.mipmap.ic_launcher"
**Solución**: Los recursos siguen existiendo, solo sincroniza:
```
File → Sync Project with Gradle Files
```

#### Error 2: "Cannot resolve symbol 'R'"
**Solución**: 
1. `Build → Clean Project`
2. `Build → Rebuild Project`

#### Error 3: La app se cierra inmediatamente
**Causa**: Puede ser que el MainActivity no encuentre recursos
**Solución**: Vamos a verificar juntos

### 🚀 Ejecutar la App:

**Opción 1: Desde Android Studio**
1. Asegúrate de que el emulador esté corriendo
2. Selecciona el emulador en la barra superior
3. Haz clic en el botón ▶️ verde "Run"
4. Espera a que se instale (primera vez puede tardar)

**Opción 2: Desde la terminal**
```bash
./gradlew installDebug
```

### 📱 Verificar que la App se Instaló:

En el emulador:
1. Abre el **App Drawer** (lista de apps)
2. Busca "**FactuSoft**" o "**Factusoft**"
3. Debería aparecer con tu icono

---

## 3️⃣ Verificar Logs del Emulador

Si la app se cierra, ve los logs:

1. **Abre Logcat** en Android Studio (parte inferior)
2. Filtra por: `com.tuempresa.factusoft`
3. Busca líneas en **rojo** (errores)
4. Copia el error y lo revisamos

---

## 4️⃣ Agregar Más Previews (Opcional)

Si quieres ver previews de otras pantallas, agrega esto al final de cualquier Activity:

```kotlin
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NombreScreenPreview() {
    MaterialTheme {
        NombreScreen(
            // Parámetros simulados
            onBackPressed = { },
            // ... más parámetros
        )
    }
}
```

Ejemplo para CustomersActivity:
```kotlin
@Preview(showBackground = true)
@Composable
fun CustomerCardPreview() {
    MaterialTheme {
        CustomerCard(
            customer = Customer(
                1, 
                "Juan", 
                "Pérez", 
                "+52 123456789", 
                "juan@email.com", 
                "Av. Principal 123"
            ),
            onEdit = { },
            onDelete = { }
        )
    }
}
```

---

## 5️⃣ Comando Rápido para Limpiar y Recompilar

Si tienes problemas, ejecuta esto en la terminal:

```bash
# Limpiar y reconstruir
./gradlew clean
./gradlew assembleDebug

# O todo junto:
./gradlew clean assembleDebug --refresh-dependencies
```

---

## 🆘 Si Sigue Sin Funcionar:

Necesito que me compartas:

1. **El error en Logcat** (las líneas rojas)
2. **El resultado de**: `./gradlew build` (si hay errores)
3. **Screenshot** del Android Studio mostrando el error

Y te ayudaré a solucionarlo inmediatamente.

---

## ✅ Checklist Rápido:

- [ ] ¿El proyecto compila sin errores? (`Build → Make Project`)
- [ ] ¿El emulador está corriendo?
- [ ] ¿Sincronizaste Gradle? (`File → Sync Project with Gradle Files`)
- [ ] ¿La app aparece en el emulador? (Busca "FactuSoft" en apps)
- [ ] ¿Intentaste limpiar y reconstruir? (`Clean + Rebuild`)

---

## 📸 Ubicación de Botones en Android Studio:

```
┌─────────────────────────────────────────────┐
│ File Edit View ... Build Run Tools ...     │
├─────────────────────────────────────────────┤
│ [▶] [⚙] [Pixel 8 API 35 ▼]  [Code|Split|Design] │
│           ↑                           ↑     │
│     Botón Run                   Vista Preview │
└─────────────────────────────────────────────┘
```

---

**¿Qué error específico te muestra?** Compártelo y lo solucionamos juntos. 🚀

